package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    @SerializedName("_id") val id: String? = null,
    val id_alt: String? = null,
    @SerializedName("conversationId") val conversationId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("senderRole") val senderRole: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("content") val contentParam: String? = null,
    @SerializedName("contractId") val contractId: String? = null,
    @SerializedName("pdfUrl") val pdfUrl: String? = null,
    @SerializedName("isContractMessage") val isContractMessage: Boolean? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    val messageId: String
        get() = id ?: id_alt ?: UUID.randomUUID().toString()
    
    val content: String
        get() = text ?: contentParam ?: ""
    
    val formattedTime: String
        get() {
            if (createdAt == null) return ""
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(createdAt)
                if (date == null) return ""
                
                val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                outputFormat.format(date)
            } catch (e: Exception) {
                ""
            }
        }
}

