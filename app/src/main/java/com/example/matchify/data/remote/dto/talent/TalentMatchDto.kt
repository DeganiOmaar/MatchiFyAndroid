package com.example.matchify.data.remote.dto.talent

import com.google.gson.annotations.SerializedName

/**
 * DTO pour un talent avec score de matching IA
 * Structure similaire à BestMatchMissionDto
 */
data class TalentMatchDto(
    @SerializedName("talentId") val talentId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("skills") val skills: List<String>?,
    @SerializedName("talent") val talent: List<String>?, // Catégories de talent
    @SerializedName("description") val description: String?,
    @SerializedName("matchScore") val matchScore: Int,
    @SerializedName("reasoning") val reasoning: String?,
    @SerializedName("cvUrl") val cvUrl: String?
)

/**
 * DTO pour la réponse de filtrage/scoring des talents
 */
data class TalentMatchResponseDto(
    @SerializedName("talents") val talents: List<TalentMatchDto>,
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)

/**
 * DTO pour les filtres de recherche de talents
 */
data class TalentFilterRequestDto(
    @SerializedName("missionId") val missionId: String? = null, // Filtrer par mission spécifique
    @SerializedName("skills") val skills: List<String>? = null, // Filtrer par compétences
    @SerializedName("minScore") val minScore: Int? = null, // Score minimum
    @SerializedName("location") val location: String? = null, // Filtrer par localisation
    @SerializedName("experienceLevel") val experienceLevel: String? = null, // ENTRY, INTERMEDIATE, EXPERT
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)

