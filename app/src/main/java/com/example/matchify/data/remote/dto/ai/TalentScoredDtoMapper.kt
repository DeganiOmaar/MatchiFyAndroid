package com.example.matchify.data.remote.dto.ai

import com.example.matchify.domain.model.TalentScored

/**
 * Mapper pour convertir TalentScoredDto en modèle domaine TalentScored
 */
object TalentScoredDtoMapper {
    fun toDomain(dto: TalentScoredDto): TalentScored {
        return TalentScored(
            talentId = dto.talentId,
            fullName = dto.fullName,
            score = dto.score,
            skillMatch = dto.skillMatch,
            experienceMatch = dto.experienceMatch,
            matchingSkills = dto.matchingSkills,
            missionSkills = dto.missionSkills
        )
    }
    
    fun toDomainList(dtos: List<TalentScoredDto>): List<TalentScored> {
        return dtos.map { toDomain(it) }
    }
}

