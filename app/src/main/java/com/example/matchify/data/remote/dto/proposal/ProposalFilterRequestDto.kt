package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

data class ProposalFilterRequestDto(
    @SerializedName("missionId")
    val missionId: String? = null,
    
    @SerializedName("minScore")
    val minScore: Int? = null,
    
    @SerializedName("maxScore")
    val maxScore: Int? = null,
    
    @SerializedName("status")
    val status: String? = null,
    
    @SerializedName("skills")
    val skills: List<String>? = null,
    
    @SerializedName("talentLocation")
    val talentLocation: String? = null,
    
    @SerializedName("minBudget")
    val minBudget: Int? = null,
    
    @SerializedName("maxBudget")
    val maxBudget: Int? = null,
    
    @SerializedName("sortBy")
    val sortBy: String? = null,
    
    @SerializedName("sortOrder")
    val sortOrder: String? = null
)
