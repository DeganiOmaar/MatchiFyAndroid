package com.example.matchify.ui.interviews

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.api.InterviewApi
import com.example.matchify.domain.model.Interview

class InterviewRepository(
    private val interviewApi: InterviewApi,
    private val authPreferences: AuthPreferences
) {
    suspend fun getMyInterviews(): List<Interview> {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        val isRecruiter = authPreferences.currentRole.value == "recruiter"
        return if (isRecruiter) {
            interviewApi.getRecruiterInterviews("Bearer $token").map { it.toDomain() }
        } else {
            interviewApi.getTalentInterviews("Bearer $token").map { it.toDomain() }
        }
    }
    
    suspend fun getInterviewById(interviewId: String): Interview {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        return interviewApi.getInterviewById(interviewId, "Bearer $token").toDomain()
    }
    
    suspend fun createInterview(
        proposalId: String,
        scheduledAt: String,
        notes: String?,
        autoGenerateMeetLink: Boolean,
        meetLink: String? = null
    ): Interview {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        
        val request = com.example.matchify.data.remote.dto.interview.CreateInterviewRequestDto(
            proposalId = proposalId,
            scheduledAt = scheduledAt,
            notes = notes,
            autoGenerateMeetLink = autoGenerateMeetLink,
            meetLink = meetLink
        )
        return interviewApi.createInterview(request, "Bearer $token").toDomain()
    }
    
    suspend fun cancelInterview(interviewId: String): Interview {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        return interviewApi.cancelInterview(interviewId, "Bearer $token").toDomain()
    }
    
    val isRecruiter: Boolean
        get() = authPreferences.currentRole.value == "recruiter"
}
