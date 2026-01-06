package com.example.matchify.data.remote.dto.ai

import com.example.matchify.domain.model.TalentCandidate

/**
 * Mapper pour convertir TalentCandidateDto en TalentCandidate (modèle domaine)
 */
fun TalentCandidateDto.toDomain(
    talentDetails: com.example.matchify.domain.model.UserModel? = null
): TalentCandidate {
    return TalentCandidate(
        talentId = talentId,
        score = score,
        reasons = reasons,
        matchBreakdown = matchBreakdown?.toDomain(),
        // Informations du talent si disponibles
        fullName = talentDetails?.fullName ?: "Talent",
        email = talentDetails?.email ?: "",
        profileImage = talentDetails?.profileImageUrl,
        location = talentDetails?.location,
        skills = talentDetails?.skills ?: emptyList(),
        description = talentDetails?.description
    )
}

fun MatchBreakdownDto.toDomain(): TalentCandidate.MatchBreakdown {
    return TalentCandidate.MatchBreakdown(
        skillsMatch = skillsMatch,
        experienceMatch = experienceMatch,
        locationMatch = locationMatch,
        otherFactors = otherFactors
    )
}

fun List<TalentCandidateDto>.toDomain(
    talentDetailsMap: Map<String, com.example.matchify.domain.model.UserModel> = emptyMap()
): List<TalentCandidate> {
    return map { dto ->
        dto.toDomain(talentDetailsMap[dto.talentId])
    }
}

