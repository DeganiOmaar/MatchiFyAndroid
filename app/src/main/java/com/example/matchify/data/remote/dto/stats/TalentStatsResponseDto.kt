package com.example.matchify.data.remote.dto.stats

import com.google.gson.annotations.SerializedName

data class TalentStatsResponseDto(
    @SerializedName("totalProposalsSent")
    val totalProposalsSent: Int,
    @SerializedName("totalProposalsAccepted")
    val totalProposalsAccepted: Int,
    @SerializedName("totalProposalsRefused")
    val totalProposalsRefused: Int
)

