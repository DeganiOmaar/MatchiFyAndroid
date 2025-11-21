package com.example.matchify.data.remote.dto.alert

import com.example.matchify.domain.model.Alert
import com.google.gson.annotations.SerializedName

enum class AlertType {
    @SerializedName("PROPOSAL_SUBMITTED")
    PROPOSAL_SUBMITTED,
    
    @SerializedName("PROPOSAL_ACCEPTED")
    PROPOSAL_ACCEPTED,
    
    @SerializedName("PROPOSAL_REFUSED")
    PROPOSAL_REFUSED
}

data class AlertDto(
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("_id")
    val _id: String? = null,
    
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("type")
    val type: AlertType,
    
    @SerializedName("missionId")
    val missionId: String,
    
    @SerializedName("proposalId")
    val proposalId: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("isRead")
    val isRead: Boolean,
    
    @SerializedName("talentId")
    val talentId: String? = null,
    
    @SerializedName("talentName")
    val talentName: String? = null,
    
    @SerializedName("talentProfileImage")
    val talentProfileImage: String? = null,
    
    @SerializedName("recruiterId")
    val recruiterId: String? = null,
    
    @SerializedName("recruiterName")
    val recruiterName: String? = null,
    
    @SerializedName("recruiterProfileImage")
    val recruiterProfileImage: String? = null,
    
    @SerializedName("missionTitle")
    val missionTitle: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

fun AlertDto.toDomain(): Alert {
    return Alert(
        id = _id ?: id ?: "",
        userId = userId,
        type = when (type) {
            AlertType.PROPOSAL_SUBMITTED -> Alert.AlertType.PROPOSAL_SUBMITTED
            AlertType.PROPOSAL_ACCEPTED -> Alert.AlertType.PROPOSAL_ACCEPTED
            AlertType.PROPOSAL_REFUSED -> Alert.AlertType.PROPOSAL_REFUSED
        },
        missionId = missionId,
        proposalId = proposalId,
        title = title,
        message = message,
        isRead = isRead,
        talentId = talentId,
        talentName = talentName,
        talentProfileImage = talentProfileImage,
        recruiterId = recruiterId,
        recruiterName = recruiterName,
        recruiterProfileImage = recruiterProfileImage,
        missionTitle = missionTitle,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

