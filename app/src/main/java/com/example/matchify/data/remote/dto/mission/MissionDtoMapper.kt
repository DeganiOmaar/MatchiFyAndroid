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
        createdAt = createdAt,
        updatedAt = updatedAt,
        proposalsCount = proposalsCount,
        interviewingCount = interviewingCount,
        hasApplied = hasApplied,
        isFavorite = isFavorite
    )
}

object MissionDtoMapper {
    fun toDomain(dto: MissionDto): Mission {
        return Mission(
            id = dto.id,
            _id = dto._id,
            title = dto.title,
            description = dto.description,
            duration = dto.duration,
            budget = dto.budget,
            skills = dto.skills,
            recruiterId = dto.recruiterId,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt,
            proposalsCount = dto.proposalsCount,
            interviewingCount = dto.interviewingCount,
            hasApplied = dto.hasApplied,
            isFavorite = dto.isFavorite
        )
    }
}

fun Mission.toCreateRequest(): CreateMissionRequest {
    return CreateMissionRequest(
        title = title,
        description = description ?: "",
        duration = duration ?: "",
        budget = budget ?: 0,
        skills = skills ?: emptyList(),
        experienceLevel = "ENTRY" // Valeur par défaut si non disponible
    )
}

fun Mission.toUpdateRequest(): UpdateMissionRequest {
    return UpdateMissionRequest(
        title = title,
        description = description ?: "",
        duration = duration ?: "",
        budget = budget ?: 0,
        skills = skills ?: emptyList()
    )
}

