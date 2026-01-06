package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

data class ProposalFilterResponseDto(
    @SerializedName("proposals")
    val proposals: List<ProposalDto>,
    
    @SerializedName("averageScore")
    val averageScore: Double? = null,
    
    @SerializedName("count")
    val count: Int = 0
)
