package com.example.matchify.domain.model

data class Alert(
    val id: String,
    val userId: String,
    val type: AlertType,
    val missionId: String,
    val proposalId: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val talentId: String? = null,
    val talentName: String? = null,
    val talentProfileImage: String? = null,
    val recruiterId: String? = null,
    val recruiterName: String? = null,
    val recruiterProfileImage: String? = null,
    val missionTitle: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    enum class AlertType {
        PROPOSAL_SUBMITTED,
        PROPOSAL_ACCEPTED,
        PROPOSAL_REFUSED
    }
    
    val profileImageUrl: String?
        get() {
            if (!talentProfileImage.isNullOrEmpty()) return talentProfileImage
            if (!recruiterProfileImage.isNullOrEmpty()) return recruiterProfileImage
            return null
        }
    
    val userName: String?
        get() {
            if (!talentName.isNullOrEmpty()) return talentName
            if (!recruiterName.isNullOrEmpty()) return recruiterName
            return null
        }
}

