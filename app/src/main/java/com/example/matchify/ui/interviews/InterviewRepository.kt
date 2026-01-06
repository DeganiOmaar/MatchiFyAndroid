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
        return interviewApi.getMyInterviews("Bearer $token").map { it.toDomain() }
    }
    
    suspend fun getInterviewById(interviewId: String): Interview {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        return interviewApi.getInterviewById(interviewId, "Bearer $token").toDomain()
    }
    
    suspend fun createInterview(
        proposalId: String,
        date: String,
        time: String,
        duration: Int,
        type: String,
        notes: String?
    ): Interview {
        val token = authPreferences.getTokenValue() ?: throw Exception("No auth token")
        
        // Combine date and time to ISO 8601
        // date is yyyy-MM-dd, time is HH:mm
        val scheduledAt = "${date}T${time}:00.000Z" // Simple concatenation for now, assuming local time is roughly UTC or backend handles it.
        // Ideally use proper OffsetDateTime
        
        val request = com.example.matchify.data.remote.dto.interview.CreateInterviewRequestDto(
            proposalId = proposalId,
            scheduledAt = scheduledAt,
            notes = notes,
            autoGenerateMeetLink = true
        )
        return interviewApi.createInterview(request, "Bearer $token").toDomain()
    }
}
