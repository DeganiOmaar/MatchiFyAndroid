package com.example.matchify.data.remote.dto.skill

import com.example.matchify.domain.model.Skill

fun SkillDto.toDomain(): Skill {
    return Skill(
        id = id,
        _id = _id,
        name = name,
        source = source,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

