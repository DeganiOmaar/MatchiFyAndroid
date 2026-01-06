package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.interview.CreateInterviewRequest
import com.example.matchify.data.remote.dto.interview.UpdateInterviewRequest
import com.example.matchify.domain.model.Interview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class InterviewRepository(
    private val api: InterviewApi,
    private val prefs: AuthPreferences
) {
    
    /**
     * Créer une interview
     */
    suspend fun createInterview(
        proposalId: String,
        scheduledAt: Date,
        meetLink: String? = null,
        notes: String? = null,
        autoGenerateMeetLink: Boolean = false
    ): Interview = withContext(Dispatchers.IO) {
        // Convertir Date en ISO 8601
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        formatter.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val scheduledAtStr = formatter.format(scheduledAt)
        
        val request = CreateInterviewRequest(
            proposalId = proposalId,
            scheduledAt = scheduledAtStr,
            meetLink = meetLink,
            notes = notes,
            autoGenerateMeetLink = autoGenerateMeetLink
        )
        api.createInterview(request).toDomain()
    }
    
    /**
     * Récupérer les interviews du recruteur
     */
    suspend fun getRecruiterInterviews(): List<Interview> = withContext(Dispatchers.IO) {
        api.getRecruiterInterviews().map { it.toDomain() }
    }
    
    /**
     * Récupérer les interviews du talent
     */
    suspend fun getTalentInterviews(): List<Interview> = withContext(Dispatchers.IO) {
        api.getTalentInterviews().map { it.toDomain() }
    }
    
    /**
     * Mettre à jour une interview
     */
    suspend fun updateInterview(
        interviewId: String,
        scheduledAt: String? = null,
        meetLink: String? = null,
        status: String? = null,
        notes: String? = null
    ): Interview = withContext(Dispatchers.IO) {
        val request = UpdateInterviewRequest(
            scheduledAt = scheduledAt,
            meetLink = meetLink,
            status = status,
            notes = notes
        )
        api.updateInterview(interviewId, request).toDomain()
    }
    
    /**
     * Annuler une interview
     */
    suspend fun cancelInterview(interviewId: String, cancellationReason: String): Interview = withContext(Dispatchers.IO) {
        val request = com.example.matchify.data.remote.dto.interview.CancelInterviewRequest(cancellationReason)
        api.cancelInterview(interviewId, request).toDomain()
    }
}

