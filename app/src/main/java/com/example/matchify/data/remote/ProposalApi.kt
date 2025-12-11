package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.proposal.CreateProposalRequest
import com.example.matchify.data.remote.dto.proposal.ProposalDto
import com.example.matchify.data.remote.dto.proposal.UpdateProposalStatusRequest
import retrofit2.http.*

interface ProposalApi {
    
    @POST("proposals")
    suspend fun createProposal(@Body request: CreateProposalRequest): ProposalDto
    
    @GET("proposals/talent")
    suspend fun getTalentProposals(
        @Query("status") status: String? = null,
        @Query("archived") archived: Boolean? = null
    ): List<ProposalDto>
    
    @GET("proposals/recruiter")
    suspend fun getRecruiterProposals(): List<ProposalDto>
    
    @GET("proposals/{id}")
    suspend fun getProposalById(@Path("id") id: String): ProposalDto
    
    @PATCH("proposals/{id}/status")
    suspend fun updateProposalStatus(
        @Path("id") id: String,
        @Body request: UpdateProposalStatusRequest
    ): ProposalDto
    
    @PATCH("proposals/{id}/archive")
    suspend fun archiveProposal(@Path("id") id: String): ProposalDto
    
    @DELETE("proposals/{id}")
    suspend fun deleteProposal(@Path("id") id: String): ProposalDto
    
    @GET("proposals/mission/{missionId}/count")
    suspend fun getMissionProposalsCount(@Path("missionId") missionId: String): Map<String, Any>
    
    // Récupérer les missions du recruteur pour le filtre
    // GET /recruiter/missions
    @GET("recruiter/missions")
    suspend fun getRecruiterMissions(): List<com.example.matchify.data.remote.dto.mission.MissionDto>
    
    // Récupérer les propositions pour une mission avec tri AI optionnel
    // GET /recruiter/proposals/mission/{missionId}?sort=ai
    @GET("recruiter/proposals/mission/{missionId}")
    suspend fun getProposalsForMission(
        @Path("missionId") missionId: String,
        @Query("sort") sort: String? = null // "ai" pour tri AI
    ): MissionProposalsResponseDto
    
    // Rechercher les propositions par titre de mission
    // GET /recruiter/proposals?title={title}
    @GET("recruiter/proposals")
    suspend fun searchProposalsByMissionTitle(
        @Query("title") title: String
    ): List<MissionProposalsSearchResultDto>
}

/**
 * Réponse avec mission et ses propositions
 */
data class MissionProposalsResponseDto(
    val mission: com.example.matchify.data.remote.dto.mission.MissionDto,
    val proposals: List<ProposalDto>
)

/**
 * Résultat de recherche de propositions par titre de mission
 */
data class MissionProposalsSearchResultDto(
    val mission: com.example.matchify.data.remote.dto.mission.MissionDto,
    val proposalCount: Int,
    val proposals: List<ProposalDto>
)

