package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * DTO pour un talent scoré par l'IA pour une mission
 * Correspond à la réponse de GET /ai/mission/:missionId/talents
 */
data class TalentScoredDto(
    @SerializedName("talentId") val talentId: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("score") val score: Int, // 0-100, score global
    @SerializedName("skillMatch") val skillMatch: Double, // 0-1, similarité de compétences
    @SerializedName("experienceMatch") val experienceMatch: Double, // 0-1, adéquation expérience
    @SerializedName("matchingSkills") val matchingSkills: List<String>, // compétences de la mission retrouvées chez le talent
    @SerializedName("missionSkills") val missionSkills: List<String> // compétences requises par la mission
)

