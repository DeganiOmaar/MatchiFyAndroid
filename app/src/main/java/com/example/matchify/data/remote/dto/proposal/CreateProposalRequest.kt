package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

data class CreateProposalRequest(
    @SerializedName("missionId") val missionId: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("proposalContent") val proposalContent: String, // Requis comme dans iOS
    @SerializedName("proposedBudget") val proposedBudget: Int? = null,
    @SerializedName("estimatedDuration") val estimatedDuration: String? = null
)

