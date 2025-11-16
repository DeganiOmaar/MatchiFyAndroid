package com.example.matchify.data.remote.dto

import com.example.matchify.domain.model.Mission
import com.google.gson.annotations.SerializedName

// Request DTOs
data class CreateMissionRequest(
    val title: String,
    val description: String,
    val duration: String,
    val budget: Int,
    val skills: List<String>
)

data class UpdateMissionRequest(
    val title: String? = null,
    val description: String? = null,
    val duration: String? = null,
    val budget: Int? = null,
    val skills: List<String>? = null
)

// Response DTOs
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

// Mapper
fun MissionDto.toDomain(): Mission {
    return Mission(
        id = id,
        _id = _id,
        title = title,
        description = description,
        duration = duration,
        budget = budget,
        skills = skills,
        recruiterId = recruiterId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Mission.toCreateRequest(): CreateMissionRequest {
    return CreateMissionRequest(
        title = title,
        description = description,
        duration = duration,
        budget = budget,
        skills = skills
    )
}

fun Mission.toUpdateRequest(): UpdateMissionRequest {
    return UpdateMissionRequest(
        title = title,
        description = description,
        duration = duration,
        budget = budget,
        skills = skills
    )
}

