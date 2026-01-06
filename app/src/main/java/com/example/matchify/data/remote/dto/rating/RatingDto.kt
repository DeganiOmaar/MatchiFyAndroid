package com.example.matchify.data.remote.dto.rating

import com.example.matchify.domain.model.Rating
import com.google.gson.annotations.SerializedName

/**
 * DTO pour un rating
 */
data class RatingDto(
    @SerializedName("_id")
    val _id: String? = null,
    
    @SerializedName("id")
    val id: String? = null,
    
    @SerializedName("talentId")
    val talentId: String,
    
    @SerializedName("recruiterId")
    val recruiterId: String,
    
    @SerializedName("missionId")
    val missionId: String? = null,
    
    @SerializedName("score")
    val score: Int, // 1 à 5
    
    @SerializedName("recommended")
    val recommended: Boolean = false, // Recommandation du talent
    
    @SerializedName("comment")
    val comment: String? = null,
    
    @SerializedName("tags")
    val tags: List<String>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * Mapper pour convertir RatingDto en modèle domaine Rating
 */
fun RatingDto.toDomain(): Rating {
    return Rating(
        id = _id ?: id ?: "",
        talentId = talentId,
        recruiterId = recruiterId,
        missionId = missionId,
        score = score,
        recommended = recommended,
        comment = comment,
        tags = tags ?: emptyList(),
        createdAt = createdAt ?: "",
        recruiterName = null,
        recruiterAvatarUrl = null
    )
}

