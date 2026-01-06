package com.example.matchify.data.remote.dto.proposal

import com.google.gson.annotations.SerializedName

/**
 * DTO pour les filtres de recherche de propositions avec IA
 */
data class ProposalFilterRequestDto(
    @SerializedName("missionId") val missionId: String? = null, // Filtrer par mission spécifique
    @SerializedName("minScore") val minScore: Int? = null, // Score IA minimum requis (0-100)
    @SerializedName("maxScore") val maxScore: Int? = null, // Score IA maximum (0-100)
    @SerializedName("status") val status: String? = null, // NOT_VIEWED, VIEWED, ACCEPTED, REFUSED
    @SerializedName("skills") val skills: List<String>? = null, // Filtrer par compétences du talent
    @SerializedName("talentLocation") val talentLocation: String? = null, // Localisation du talent
    @SerializedName("minBudget") val minBudget: Int? = null, // Budget proposé minimum
    @SerializedName("maxBudget") val maxBudget: Int? = null, // Budget proposé maximum
    @SerializedName("sortBy") val sortBy: String? = null, // "score", "date", "budget"
    @SerializedName("sortOrder") val sortOrder: String? = null, // "asc", "desc"
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null
)



