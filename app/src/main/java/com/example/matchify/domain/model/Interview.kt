package com.example.matchify.domain.model

import java.text.SimpleDateFormat
import java.util.*

data class Interview(
    val id: String?,
    val proposalId: String,
    val missionId: String?,
    val recruiterId: String,
    val talentId: String,
    val scheduledAt: String,
    val meetLink: String,
    val status: InterviewStatus,
    val notes: String?,
    val source: InterviewSource,
    val createdAt: String?,
    val updatedAt: String?,
    val talent: TalentInfo?,
    val talentName: String?,
    val talentEmail: String?,
    val recruiterName: String?,
    val missionTitle: String?
) {
    val interviewId: String
        get() = id ?: ""
    
    val formattedScheduledDate: String
        get() {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(scheduledAt)
                val outputFormat = SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.FRENCH)
                date?.let { outputFormat.format(it) } ?: scheduledAt
            } catch (e: Exception) {
                scheduledAt
            }
        }
    
    val isUpcoming: Boolean
        get() {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(scheduledAt)
                date?.after(Date()) ?: false
            } catch (e: Exception) {
                false
            }
        }
    
    val isPast: Boolean
        get() = !isUpcoming
}

enum class InterviewStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}

enum class InterviewSource(val displayName: String) {
    ZOOM("Zoom"),
    GOOGLE("Google Meet"),
    MANUAL("Manuel")
}


