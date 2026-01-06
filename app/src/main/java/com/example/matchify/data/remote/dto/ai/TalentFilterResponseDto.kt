package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * DTO pour un candidat talent avec score IA
 * Correspond à la structure candidates[] dans talent-filter-response.dto.ts
 */
data class TalentCandidateDto(
    @SerializedName("talentId") val talentId: String,
    @SerializedName("score") val score: Int, // Score IA (0-100)
    @SerializedName("reasons") val reasons: String? = null, // Raisons du score
    @SerializedName("matchBreakdown") val matchBreakdown: MatchBreakdownDto? = null // Détails du matching
)

/**
 * DTO pour les détails du matching
 */
data class MatchBreakdownDto(
    @SerializedName("skillsMatch") val skillsMatch: Double? = null, // Pourcentage de correspondance des compétences
    @SerializedName("experienceMatch") val experienceMatch: Double? = null, // Correspondance de l'expérience
    @SerializedName("locationMatch") val locationMatch: Double? = null, // Correspondance de la localisation
    @SerializedName("otherFactors") val otherFactors: Map<String, Any>? = null // Autres facteurs
)

/**
 * DTO pour la réponse de filtrage IA des talents
 * Correspond à talent-filter-response.dto.ts côté backend
 */
data class TalentFilterResponseDto(
    @SerializedName("missionId") val missionId: String,
    @SerializedName("candidates") val candidates: List<TalentCandidateDto>,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("pageSize") val pageSize: Int? = null
)

