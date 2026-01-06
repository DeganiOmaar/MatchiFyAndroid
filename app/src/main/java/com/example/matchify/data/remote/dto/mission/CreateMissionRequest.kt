package com.example.matchify.data.remote.dto.mission

import com.google.gson.annotations.SerializedName

data class CreateMissionRequest(
    val title: String,
    val description: String,
    val duration: String,
    val budget: Int,
    val skills: List<String>,
    @SerializedName("experienceLevel") val experienceLevel: String // ENTRY, INTERMEDIATE, or EXPERT
)

