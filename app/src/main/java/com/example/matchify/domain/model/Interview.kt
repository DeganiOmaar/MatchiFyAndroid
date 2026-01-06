package com.example.matchify.domain.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

enum class InterviewStatus(val value: String, val displayName: String) {
    @SerializedName("SCHEDULED")
    SCHEDULED("SCHEDULED", "Planifié"),
    
    @SerializedName("COMPLETED")
    COMPLETED("COMPLETED", "Terminé"),
    
    @SerializedName("CANCELLED")
    CANCELLED("CANCELLED", "Annulé")
}

enum class InterviewSource(val value: String, val displayName: String) {
    @SerializedName("MANUAL")
    MANUAL("MANUAL", "Manuel"),
    
    @SerializedName("ZOOM")
    ZOOM("ZOOM", "Zoom"),
    
    @SerializedName("GOOGLE")
    GOOGLE("GOOGLE", "Google Meet")
}

data class Interview(
    @SerializedName("_id") val id: String? = null,
    val id_alt: String? = null,
    @SerializedName("proposalId") val proposalId: String,
    @SerializedName("missionId") val missionId: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("scheduledAt") val scheduledAt: String, // ISO 8601 format
    @SerializedName("meetLink") val meetLink: String,
    @SerializedName("status") val status: InterviewStatus,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("source") val source: InterviewSource = InterviewSource.MANUAL,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    // Champs additionnels pour l'affichage (peuvent venir du backend ou être calculés)
    @SerializedName("talent") val talent: TalentInfo? = null,
    @SerializedName("talentName") val talentName: String? = null, // Fallback pour compatibilité
    @SerializedName("talentEmail") val talentEmail: String? = null, // Fallback pour compatibilité
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("missionTitle") val missionTitle: String? = null
) {
    val interviewId: String
        get() = id ?: id_alt ?: UUID.randomUUID().toString()
    
    val scheduledDate: Date?
        get() = try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(scheduledAt)
        } catch (e: Exception) {
            null
        }
    
    val formattedScheduledDate: String
        get() = scheduledDate?.let {
            val formatter = SimpleDateFormat("dd MMM yyyy 'à' HH:mm", Locale.FRENCH)
            formatter.format(it)
        } ?: scheduledAt
    
    val isUpcoming: Boolean
        get() = status == InterviewStatus.SCHEDULED && scheduledDate?.let { it.after(Date()) } == true
    
    val isPast: Boolean
        get() = scheduledDate?.let { it.before(Date()) } == true || status == InterviewStatus.COMPLETED
}

