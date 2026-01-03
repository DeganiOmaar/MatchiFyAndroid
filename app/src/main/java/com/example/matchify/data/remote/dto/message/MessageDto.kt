package com.example.matchify.data.remote.dto.message

import com.example.matchify.data.remote.dto.deliverable.DeliverableDto
import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderRole") val senderRole: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("content") val content: String? = null,
    @SerializedName("contractId") val contractId: String? = null,
    @SerializedName("pdfUrl") val pdfUrl: String? = null,
    @SerializedName("isContractMessage") val isContractMessage: Boolean? = null,
    @SerializedName("deliverableId") val deliverable: DeliverableDto? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

