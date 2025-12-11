package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.conversation.ConversationDtoMapper
import com.example.matchify.data.remote.dto.conversation.CreateConversationRequest
import com.example.matchify.data.remote.dto.conversation.CreateMessageRequest
import com.example.matchify.data.remote.dto.message.MessageDtoMapper
import com.example.matchify.domain.model.Conversation
import com.example.matchify.domain.model.Message

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
}

