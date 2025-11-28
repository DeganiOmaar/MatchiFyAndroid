package com.example.matchify.data.remote.dto.mission

import com.google.gson.annotations.SerializedName

/**
 * Modèle pour les missions Best Match avec AI
 * Même structure que iOS
 */
data class BestMatchMissionDto(
    @SerializedName("missionId") val missionId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("budget") val budget: Int,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("recruiterId") val recruiterId: String,
    @SerializedName("matchScore") val matchScore: Int,
    @SerializedName("reasoning") val reasoning: String
)

data class BestMatchMissionsResponseDto(
    @SerializedName("missions") val missions: List<BestMatchMissionDto>
)


