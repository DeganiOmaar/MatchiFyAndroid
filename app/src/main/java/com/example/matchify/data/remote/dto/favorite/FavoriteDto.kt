package com.example.matchify.data.remote.dto.favorite

import com.example.matchify.data.remote.dto.mission.MissionDto
import com.google.gson.annotations.SerializedName

data class FavoriteDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("missionId") val missionId: String,
    @SerializedName("talentId") val talentId: String,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("mission") val mission: MissionDto? = null // Populated by backend
)

