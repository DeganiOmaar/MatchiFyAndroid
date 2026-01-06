package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.interview.CancelInterviewRequest
import com.example.matchify.data.remote.dto.interview.CreateInterviewRequest
import com.example.matchify.data.remote.dto.interview.InterviewDto
import com.example.matchify.data.remote.dto.interview.UpdateInterviewRequest
import retrofit2.http.*

interface InterviewApi {
    
    /**
     * Créer une interview à partir d'une proposal
     * POST /interviews
     * Rôle: recruteur
     */
    @POST("interviews")
    suspend fun createInterview(@Body request: CreateInterviewRequest): InterviewDto
    
    /**
     * Lister les interviews d'un recruteur
     * GET /interviews/recruiter
     * Rôle: recruteur
     */
    @GET("interviews/recruiter")
    suspend fun getRecruiterInterviews(): List<InterviewDto>
    
    /**
     * Lister les interviews d'un talent
     * GET /interviews/talent
     * Rôle: talent
     */
    @GET("interviews/talent")
    suspend fun getTalentInterviews(): List<InterviewDto>
    
    /**
     * Modifier une interview
     * PUT /interviews/:id
     * Rôle: recruteur
     */
    @PUT("interviews/{id}")
    suspend fun updateInterview(
        @Path("id") id: String,
        @Body request: UpdateInterviewRequest
    ): InterviewDto
    
    /**
     * Annuler une interview
     * PATCH /interviews/:id/cancel
     * Rôle: recruteur
     */
    @PATCH("interviews/{id}/cancel")
    suspend fun cancelInterview(
        @Path("id") id: String,
        @Body request: CancelInterviewRequest
    ): InterviewDto
}

