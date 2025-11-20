package com.example.matchify.data.remote.dto.conversation

import com.google.gson.annotations.SerializedName

data class CreateConversationRequest(
    @SerializedName("missionId") val missionId: String? = null,
    @SerializedName("talentId") val talentId: String? = null,
    @SerializedName("recruiterId") val recruiterId: String? = null
)

