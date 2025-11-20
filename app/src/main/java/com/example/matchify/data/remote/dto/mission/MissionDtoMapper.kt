package com.example.matchify.data.remote.dto.mission

import com.example.matchify.domain.model.Mission

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
        experienceLevel = experienceLevel,
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
        skills = skills,
        experienceLevel = "INTERMEDIATE"
    )
}

fun Mission.toUpdateRequest(): UpdateMissionRequest {
    return UpdateMissionRequest(
        title = title,
        description = description,
        duration = duration,
        budget = budget,
        skills = skills,
        experienceLevel = experienceLevel
    )
}

