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
    
    @GET("proposals/mission/{missionId}/count")
    suspend fun getMissionProposalsCount(@Path("missionId") missionId: String): Map<String, Any>
}

