package com.example.matchify.data.remote.dto.talent

import com.example.matchify.domain.model.TalentMatch

/**
 * Mapper pour convertir TalentMatchDto en TalentMatch (modèle domaine)
 */
fun TalentMatchDto.toDomain(): TalentMatch {
    return TalentMatch(
        talentId = talentId,
        fullName = fullName,
        email = email,
        profileImage = profileImage,
        location = location,
        skills = skills ?: emptyList(),
        talent = talent ?: emptyList(),
        description = description,
        matchScore = matchScore,
        reasoning = reasoning,
        cvUrl = cvUrl
    )
}

fun List<TalentMatchDto>.toDomain(): List<TalentMatch> {
    return map { it.toDomain() }
}

