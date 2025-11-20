package com.example.matchify.data.remote.dto.portfolio

import com.example.matchify.domain.model.MediaItem
import com.example.matchify.domain.model.Project

fun ProjectDto.toDomain(talentIdFallback: String? = null): Project {
    return Project(
        id = id,
        id_alt = _id,
        talentId = talentId ?: talentIdFallback,
        title = title?.takeIf { it.isNotBlank() } ?: "Untitled Project",
        role = role?.takeIf { it.isNotBlank() },
        media = media?.map { it.toDomain() } ?: emptyList(),
        skills = skills ?: emptyList(),
        description = description?.takeIf { it.isNotBlank() },
        projectLink = projectLink?.takeIf { it.isNotBlank() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun MediaItemDto.toDomain(): MediaItem {
    return MediaItem(
        type = type,
        url = url,
        title = title,
        externalLink = externalLink
    )
}

