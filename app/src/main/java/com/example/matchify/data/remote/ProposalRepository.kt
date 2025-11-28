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
        message: String? = null,
        proposalContent: String, // Requis comme dans iOS
        proposedBudget: Int? = null,
        estimatedDuration: String? = null
    ): Proposal {
        val request = CreateProposalRequest(
            missionId = missionId,
            message = message,
            proposalContent = proposalContent,
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
    
    suspend fun archiveProposal(id: String): Proposal {
        val dto = apiService.proposalApi.archiveProposal(id)
        return ProposalDtoMapper.toDomain(dto)
    }
    
    suspend fun deleteProposal(id: String): Proposal {
        val dto = apiService.proposalApi.deleteProposal(id)
        return ProposalDtoMapper.toDomain(dto)
    }
    
    /**
     * Récupérer les missions du recruteur pour le filtre
     */
    suspend fun getRecruiterMissions(): List<com.example.matchify.domain.model.Mission> {
        val dtos = apiService.proposalApi.getRecruiterMissions()
        return dtos.map { com.example.matchify.data.remote.dto.mission.MissionDtoMapper.toDomain(it) }
    }
    
    /**
     * Récupérer les propositions pour une mission avec tri AI optionnel
     */
    suspend fun getProposalsForMission(
        missionId: String,
        aiSort: Boolean = false
    ): Pair<com.example.matchify.domain.model.Mission, List<Proposal>> {
        val response = apiService.proposalApi.getProposalsForMission(
            missionId = missionId,
            sort = if (aiSort) "ai" else null
        )
        val mission = com.example.matchify.data.remote.dto.mission.MissionDtoMapper.toDomain(response.mission)
        val proposals = response.proposals.map { ProposalDtoMapper.toDomain(it) }
        return Pair(mission, proposals)
    }
    
    /**
     * Rechercher les propositions par titre de mission
     */
    suspend fun searchProposalsByMissionTitle(title: String): List<Pair<com.example.matchify.domain.model.Mission, List<Proposal>>> {
        val results = apiService.proposalApi.searchProposalsByMissionTitle(title)
        return results.map { result ->
            val mission = com.example.matchify.data.remote.dto.mission.MissionDtoMapper.toDomain(result.mission)
            val proposals = result.proposals.map { ProposalDtoMapper.toDomain(it) }
            Pair(mission, proposals)
        }
    }
    
    /**
     * Générer une proposition avec AI
     */
    suspend fun generateProposalWithAI(missionId: String): String {
        val aiRepository = com.example.matchify.data.remote.AiRepository(apiService.aiApi)
        val response = aiRepository.generateProposal(missionId)
        return response.proposalContent
    }
}

