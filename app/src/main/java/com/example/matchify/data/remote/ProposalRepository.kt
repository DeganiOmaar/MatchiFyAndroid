package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.proposal.CreateProposalRequest
import com.example.matchify.data.remote.dto.proposal.ProposalDtoMapper
import com.example.matchify.data.remote.dto.proposal.UpdateProposalStatusRequest
import com.example.matchify.domain.model.Proposal

class ProposalRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    
    suspend fun createProposal(
        missionId: String,
        message: String,
        proposedBudget: Int? = null,
        estimatedDuration: String? = null
    ): Proposal {
        val request = CreateProposalRequest(
            missionId = missionId,
            message = message,
            proposedBudget = proposedBudget,
            estimatedDuration = estimatedDuration
        )
        val dto = apiService.proposalApi.createProposal(request)
        return ProposalDtoMapper.toDomain(dto)
    }
    
    suspend fun getTalentProposals(status: String? = null, archived: Boolean? = null): List<Proposal> {
        val dtos = apiService.proposalApi.getTalentProposals(status = status, archived = archived)
        return dtos.map { ProposalDtoMapper.toDomain(it) }
    }
    
    suspend fun getRecruiterProposals(): List<Proposal> {
        val dtos = apiService.proposalApi.getRecruiterProposals()
        return dtos.map { ProposalDtoMapper.toDomain(it) }
    }
    
    suspend fun getProposalById(id: String): Proposal {
        val dto = apiService.proposalApi.getProposalById(id)
        return ProposalDtoMapper.toDomain(dto)
    }
    
    suspend fun updateProposalStatus(id: String, status: String): Proposal {
        val request = UpdateProposalStatusRequest(status = status)
        val dto = apiService.proposalApi.updateProposalStatus(id, request)
        return ProposalDtoMapper.toDomain(dto)
    }
    
    suspend fun getMissionProposalsCount(missionId: String): Int {
        val response = apiService.proposalApi.getMissionProposalsCount(missionId)
        return response["count"]?.toString()?.toIntOrNull() ?: 0
    }
}

