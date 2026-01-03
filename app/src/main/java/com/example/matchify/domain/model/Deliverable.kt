package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName

data class Deliverable(
    @SerializedName("_id") val id: String,
    val messageId: String,
    val missionId: String,
    val senderId: String,
    val receiverId: String,
    val fileUrl: String? = null,
    val fileName: String? = null,
    val fileSize: Int? = null,
    val fileType: String? = null,
    val type: String? = null, // "file" or "link"
    val url: String? = null, // Unified URL field for links
    val status: String = "pending_review", // pending_review, approved, revision_requested
    val rejectionReason: String? = null,
    val approvedAt: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val deliverableId: String
        get() = id
    
    val isLink: Boolean
        get() = type == "link"
    
    val isFile: Boolean
        get() = type == "file"
    
    val displayName: String
        get() = fileName ?: if (isLink) "External Link" else "File"
    
    val statusDisplayName: String
        get() = when (status) {
            "pending_review" -> "Pending Review"
            "approved" -> "Approved"
            "revision_requested" -> "Revision Requested"
            else -> status.replace("_", " ").capitalize()
        }
}
