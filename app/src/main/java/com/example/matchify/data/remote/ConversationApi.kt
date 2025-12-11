package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.conversation.ConversationDto
import com.example.matchify.data.remote.dto.conversation.CreateConversationRequest
import com.example.matchify.data.remote.dto.conversation.CreateMessageRequest
import com.example.matchify.data.remote.dto.message.MessageDto
import retrofit2.http.*

interface ConversationApi {
    
    @GET("conversations")
    suspend fun getConversations(): List<ConversationDto>
    
    @GET("conversations/{id}")
    suspend fun getConversationById(@Path("id") id: String): ConversationDto
    
    @GET("conversations/{id}/messages")
    suspend fun getConversationMessages(@Path("id") id: String): List<MessageDto>
    
    @POST("conversations")
    suspend fun createConversation(@Body request: CreateConversationRequest): ConversationDto
    
    @POST("conversations/{id}/messages")
    suspend fun sendMessage(
        @Path("id") id: String,
        @Body request: CreateMessageRequest
    ): MessageDto
    
    @GET("conversations/unread-count")
    suspend fun getUnreadCount(): UnreadCountResponse
    
    @GET("conversations/conversations-with-unread")
    suspend fun getConversationsWithUnreadCount(): ConversationsWithUnreadCountResponse
    
    @GET("conversations/{id}/unread-count")
    suspend fun getConversationUnreadCount(@Path("id") id: String): ConversationUnreadCountResponse
    
    @POST("conversations/{id}/mark-read")
    suspend fun markConversationAsRead(@Path("id") id: String): MarkConversationReadResponse
}

data class UnreadCountResponse(
    val count: Int
)

data class ConversationsWithUnreadCountResponse(
    val count: Int
)

data class ConversationUnreadCountResponse(
    val count: Int
)

data class MarkConversationReadResponse(
    val count: Int
)

