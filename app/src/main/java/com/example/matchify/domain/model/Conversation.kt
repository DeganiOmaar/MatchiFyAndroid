package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Conversation(
    @SerializedName("_id") val id: String? = null,
    val id_alt: String? = null,
    @SerializedName("missionId") val missionId: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("lastMessageText") val lastMessageText: String? = null,
    @SerializedName("lastMessageAt") val lastMessageAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    // Talent information (for recruiter view)
    @SerializedName("talentName") val talentName: String? = null,
    @SerializedName("talentProfileImage") val talentProfileImage: String? = null,
    // Recruiter information (for talent view)
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("recruiterProfileImage") val recruiterProfileImage: String? = null
) {
    val conversationId: String
        get() = id ?: id_alt ?: UUID.randomUUID().toString()
    
    val formattedLastMessageTime: String
        get() {
            if (lastMessageAt == null) return ""
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(lastMessageAt)
                if (date == null) return ""
                
                val now = Date()
                val timeInterval = now.time - date.time
                
                val minutes = (timeInterval / 60000L).toInt()
                val hours = (timeInterval / 3600000L).toInt()
                val days = (timeInterval / 86400000L).toInt()
                
                when {
                    minutes < 1 -> "Just now"
                    minutes < 60 -> "${minutes}m ago"
                    hours < 24 -> "${hours}h ago"
                    days < 7 -> "${days}d ago"
                    else -> {
                        val formatter = SimpleDateFormat("dd/MM/yy", Locale.FRENCH)
                        formatter.format(date)
                    }
                }
            } catch (e: Exception) {
                ""
            }
        }
    
    fun getOtherUserName(isRecruiter: Boolean): String {
        return if (isRecruiter) {
            talentName ?: "Talent"
        } else {
            recruiterName ?: "Recruiter"
        }
    }
    
    fun getOtherUserProfileImageURL(isRecruiter: Boolean, baseURL: String): String? {
        val imagePath = if (isRecruiter) {
            talentProfileImage
        } else {
            recruiterProfileImage
        }
        
        if (imagePath.isNullOrBlank()) return null
        
        val fullPath = if (imagePath.startsWith("/")) imagePath else "/$imagePath"
        return "$baseURL$fullPath"
    }
}

