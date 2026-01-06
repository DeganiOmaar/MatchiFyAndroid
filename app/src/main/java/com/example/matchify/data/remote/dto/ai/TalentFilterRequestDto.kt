package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * DTO pour la requête de filtrage IA des talents
 * Correspond à talent-filter-request.dto.ts côté backend
 */
data class TalentFilterRequestDto(
    @SerializedName("missionId") val missionId: String,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("minScore") val minScore: Int? = null, // Score minimum requis (0-100)
    @SerializedName("experienceLevel") val experienceLevel: String? = null, // ENTRY, INTERMEDIATE, EXPERT
    @SerializedName("location") val location: String? = null, // Localisation du talent
    @SerializedName("skills") val skills: List<String>? = null // Compétences requises
)

