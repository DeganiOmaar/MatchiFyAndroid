package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * Réponse de l'analyse de profil AI
 * Même structure que iOS
 */
data class ProfileAnalysisResponseDto(
    @SerializedName("summary") val summary: String,
    @SerializedName("keyStrengths") val keyStrengths: List<String>,
    @SerializedName("areasToImprove") val areasToImprove: List<String>,
    @SerializedName("recommendedTags") val recommendedTags: List<String>,
    @SerializedName("profileScore") val profileScore: Int,
    @SerializedName("analyzedAt") val analyzedAt: String? = null
)


