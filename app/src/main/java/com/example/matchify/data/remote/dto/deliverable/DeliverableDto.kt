package com.example.matchify.data.remote.dto.deliverable

import com.google.gson.annotations.SerializedName

data class DeliverableDto(
    @SerializedName("_id") val id: String,
    val messageId: String,
    val missionId: String,
    val senderId: String,
    val receiverId: String,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Int? = null,
    val fileType: String? = null,
    val type: String? = null,
    val url: String? = null,
    val status: String = "pending_review",
    val rejectionReason: String? = null,
    val approvedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
