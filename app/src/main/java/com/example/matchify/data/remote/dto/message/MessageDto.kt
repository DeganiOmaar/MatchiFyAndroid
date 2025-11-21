package com.example.matchify.data.remote.dto.message

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderRole") val senderRole: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

