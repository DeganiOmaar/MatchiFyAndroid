package com.example.matchify.data.remote.dto.interview

import com.example.matchify.domain.model.Interview
import com.example.matchify.domain.model.InterviewSource
import com.example.matchify.domain.model.InterviewStatus
import com.example.matchify.domain.model.TalentInfo
import com.google.gson.annotations.SerializedName

data class InterviewDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("proposalId") val proposalId: String,
    @SerializedName("missionId") val missionId: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("scheduledAt") val scheduledAt: String,
    @SerializedName("meetLink") val meetLink: String,
    @SerializedName("status") val status: String,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("talent") val talent: TalentInfoDto? = null,
    @SerializedName("talentName") val talentName: String? = null, // Fallback pour compatibilité
    @SerializedName("talentEmail") val talentEmail: String? = null, // Fallback pour compatibilité
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("missionTitle") val missionTitle: String? = null
) {
    fun toDomain(): Interview {
        return Interview(
            id = _id ?: id,
            proposalId = proposalId,
            missionId = missionId,
            recruiterId = recruiterId,
            talentId = talentId,
            scheduledAt = scheduledAt,
            meetLink = meetLink,
            status = when (status) {
                "SCHEDULED" -> InterviewStatus.SCHEDULED
                "COMPLETED" -> InterviewStatus.COMPLETED
                "CANCELLED" -> InterviewStatus.CANCELLED
                else -> InterviewStatus.SCHEDULED
            },
            notes = notes,
            source = when (source) {
                "ZOOM" -> InterviewSource.ZOOM
                "GOOGLE" -> InterviewSource.GOOGLE
                else -> InterviewSource.MANUAL
            },
            createdAt = createdAt,
            updatedAt = updatedAt,
            talent = talent?.toDomain(),
            talentName = talentName ?: talent?.fullName, // Fallback pour compatibilité
            talentEmail = talentEmail ?: talent?.email, // Fallback pour compatibilité
            recruiterName = recruiterName,
            missionTitle = missionTitle
        )
    }
}

data class TalentInfoDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null
) {
    fun toDomain(): TalentInfo {
        return TalentInfo(
            fullName = fullName,
            email = email
        )
    }
}

