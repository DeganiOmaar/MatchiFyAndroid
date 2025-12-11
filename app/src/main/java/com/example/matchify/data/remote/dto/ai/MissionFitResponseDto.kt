package com.example.matchify.data.remote.dto.ai

import com.google.gson.annotations.SerializedName

/**
 * Réponse de l'analyse de compatibilité mission-profil
 * Même structure que iOS
 */
data class MissionFitResponseDto(
    @SerializedName("score") val score: Int,
    @SerializedName("radar") val radar: RadarDataDto,
    @SerializedName("shortSummary") val shortSummary: String
)

data class RadarDataDto(
    @SerializedName("skillsMatch") val skillsMatch: Int,
    @SerializedName("experienceFit") val experienceFit: Int,
    @SerializedName("projectRelevance") val projectRelevance: Int,
    @SerializedName("missionRequirementsFit") val missionRequirementsFit: Int,
    @SerializedName("softSkillsFit") val softSkillsFit: Int,
    // Legacy fields for backward compatibility
    @SerializedName("talentStrengthAlignment") val talentStrengthAlignment: Int? = null,
    @SerializedName("overallCoherence") val overallCoherence: Int? = null
)


