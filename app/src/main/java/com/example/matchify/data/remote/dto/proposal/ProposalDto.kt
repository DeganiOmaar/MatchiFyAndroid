package com.example.matchify.data.remote.dto.proposal

import com.example.matchify.domain.model.ProposalStatus
import com.google.gson.annotations.SerializedName

data class ProposalDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("missionId") val missionId: String,
    @SerializedName("missionTitle") val missionTitle: String? = null,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("talentName") val talentName: String? = null,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("recruiterName") val recruiterName: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("proposalContent") val proposalContent: String? = null,
    @SerializedName("proposedBudget") val proposedBudget: Int? = null,
    @SerializedName("estimatedDuration") val estimatedDuration: String? = null,
    @SerializedName("rejectionReason") val rejectionReason: String? = null,
    @SerializedName("aiScore") val aiScore: Int? = null,
    @SerializedName("talent") val talent: com.example.matchify.domain.model.TalentInfo? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
) {
    val proposalStatus: ProposalStatus
        get() = when (status) {
            "NOT_VIEWED" -> ProposalStatus.NOT_VIEWED
            "VIEWED" -> ProposalStatus.VIEWED
            "ACCEPTED" -> ProposalStatus.ACCEPTED
            "REFUSED" -> ProposalStatus.REFUSED
            else -> ProposalStatus.NOT_VIEWED
        }
}

