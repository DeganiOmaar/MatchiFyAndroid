package com.example.matchify.data.remote

import android.content.Context
import android.net.Uri
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.conversation.ConversationDtoMapper
import com.example.matchify.data.remote.dto.conversation.CreateConversationRequest
import com.example.matchify.data.remote.dto.conversation.CreateMessageRequest
import com.example.matchify.data.remote.dto.deliverable.toDomain
import com.example.matchify.data.remote.dto.message.MessageDtoMapper
import com.example.matchify.domain.model.Conversation
import com.example.matchify.domain.model.Deliverable
import com.example.matchify.domain.model.Message
import com.example.matchify.util.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ConversationRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    
    suspend fun getConversations(): List<Conversation> {
        val dtos = apiService.conversationApi.getConversations()
        return dtos.map { ConversationDtoMapper.toDomain(it) }
    }
    
    suspend fun getConversationById(id: String): Conversation {
        val dto = apiService.conversationApi.getConversationById(id)
        return ConversationDtoMapper.toDomain(dto)
    }
    
    suspend fun getConversationMessages(id: String): List<Message> {
        val dtos = apiService.conversationApi.getConversationMessages(id)
        return dtos.map { dto -> MessageDtoMapper.toDomain(dto) }
    }
    
    suspend fun createConversation(
        missionId: String? = null,
        talentId: String? = null,
        recruiterId: String? = null
    ): Conversation {
        val request = CreateConversationRequest(
            missionId = missionId,
            talentId = talentId,
            recruiterId = recruiterId
        )
        val dto = apiService.conversationApi.createConversation(request)
        return ConversationDtoMapper.toDomain(dto)
    }
    
    suspend fun sendMessage(conversationId: String, text: String): Message {
        val request = CreateMessageRequest(text = text)
        val dto = apiService.conversationApi.sendMessage(conversationId, request)
        return MessageDtoMapper.toDomain(dto)
    }
    
    suspend fun getUnreadCount(): Int {
        val response = apiService.conversationApi.getUnreadCount()
        return response.count
    }
    
    suspend fun getConversationsWithUnreadCount(): Int {
        val response = apiService.conversationApi.getConversationsWithUnreadCount()
        return response.count
    }
    
    suspend fun getConversationUnreadCount(conversationId: String): Int {
        val response = apiService.conversationApi.getConversationUnreadCount(conversationId)
        return response.count
    }
    
    suspend fun markConversationAsRead(conversationId: String): Int {
        val response = apiService.conversationApi.markConversationAsRead(conversationId)
        return response.count
    }
    
    // Deliverable methods
    suspend fun uploadDeliverable(
        conversationId: String,
        fileUri: Uri,
        context: Context
    ): Pair<Message, Deliverable> {
        val fileName = FileUtils.getFileName(fileUri, context)
        val mimeType = FileUtils.getMimeType(fileUri, context)
        val fileBytes = FileUtils.uriToByteArray(fileUri, context)
        
        // Create a temporary file
        val tempFile = File.createTempFile("upload", fileName, context.cacheDir)
        tempFile.writeBytes(fileBytes)
        
        val requestFile = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)
        
        val response = apiService.conversationApi.uploadDeliverable(conversationId, filePart)
        
        // Clean up temp file
        tempFile.delete()
        
        return Pair(
            MessageDtoMapper.toDomain(response.message),
            response.deliverable.toDomain()
        )
    }
    
    suspend fun submitLink(
        conversationId: String,
        url: String,
        title: String?
    ): Pair<Message, Deliverable> {
        val request = SubmitLinkRequest(url = url, title = title)
        val response = apiService.conversationApi.submitLink(conversationId, request)
        return Pair(
            MessageDtoMapper.toDomain(response.message),
            response.deliverable.toDomain()
        )
    }
    
    suspend fun updateDeliverableStatus(
        deliverableId: String,
        status: String,
        reason: String? = null
    ): Deliverable {
        val request = UpdateDeliverableStatusRequest(status = status, reason = reason)
        val dto = apiService.conversationApi.updateDeliverableStatus(deliverableId, request)
        return dto.toDomain()
    }
}

