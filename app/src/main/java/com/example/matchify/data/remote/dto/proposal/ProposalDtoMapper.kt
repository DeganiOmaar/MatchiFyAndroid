package com.example.matchify.data.remote.dto.proposal

import com.example.matchify.domain.model.Proposal
import com.example.matchify.domain.model.ProposalStatus

object ProposalDtoMapper {
    fun toDomain(dto: ProposalDto): Proposal {
        return Proposal(
            id = dto._id ?: dto.id,
            id_alt = null,
            missionId = dto.missionId,
            missionTitle = dto.missionTitle,
            talentId = dto.talentId,
            talentName = dto.talentName,
            recruiterId = dto.recruiterId,
            recruiterName = dto.recruiterName,
            status = dto.proposalStatus,
            message = dto.message,
            proposalContent = dto.proposalContent,
            proposedBudget = dto.proposedBudget,
            estimatedDuration = dto.estimatedDuration,
            rejectionReason = dto.rejectionReason,
            aiScore = dto.aiScore,
            talent = dto.talent,
            createdAt = dto.createdAt,
            updatedAt = dto.updatedAt
        )
    }
}

