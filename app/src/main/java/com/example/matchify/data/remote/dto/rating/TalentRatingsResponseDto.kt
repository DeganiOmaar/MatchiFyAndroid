package com.example.matchify.data.remote.dto.rating

import com.google.gson.annotations.SerializedName

/**
 * DTO pour la réponse GET /ratings/talent/:talentId
 */
data class TalentRatingsResponseDto(
    @SerializedName("talentId")
    val talentId: String,
    
    @SerializedName("averageScore")
    val averageScore: Double? = null, // moyenne simple, null si aucun rating
    
    @SerializedName("count")
    val count: Int, // nombre total de ratings
    
    @SerializedName("bayesianScore")
    val bayesianScore: Double? = null, // score bayésien pondéré, null si aucun rating
    
    @SerializedName("ratings")
    val ratings: List<RatingDto> // liste des ratings
)

