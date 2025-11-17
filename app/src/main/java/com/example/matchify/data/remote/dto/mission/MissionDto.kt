package com.example.matchify.data.remote.dto.mission

import com.google.gson.annotations.SerializedName

data class MissionDto(
    @SerializedName("_id") val _id: String? = null,
    @SerializedName("id") val id: String? = null,
    val title: String,
    val description: String,
    val duration: String,
    val budget: Int,
    val skills: List<String>,
    val recruiterId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

