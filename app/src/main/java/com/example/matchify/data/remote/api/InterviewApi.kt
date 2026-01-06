package com.example.matchify.data.remote.api

import com.example.matchify.data.remote.dto.interview.InterviewDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface InterviewApi {
    @GET("interviews/recruiter")
    suspend fun getRecruiterInterviews(
        @Header("Authorization") token: String
    ): List<InterviewDto>

    @GET("interviews/talent")
    suspend fun getTalentInterviews(
        @Header("Authorization") token: String
    ): List<InterviewDto>
    
    @GET("interviews/{id}")
    suspend fun getInterviewById(
        @Path("id") interviewId: String,
        @Header("Authorization") token: String
    ): InterviewDto
    
    @POST("interviews")
    suspend fun createInterview(
        @Body request: com.example.matchify.data.remote.dto.interview.CreateInterviewRequestDto,
        @Header("Authorization") token: String
    ): InterviewDto
    
    @retrofit2.http.PATCH("interviews/{id}/cancel")
    suspend fun cancelInterview(
        @Path("id") interviewId: String,
        @Header("Authorization") token: String
    ): InterviewDto
}
