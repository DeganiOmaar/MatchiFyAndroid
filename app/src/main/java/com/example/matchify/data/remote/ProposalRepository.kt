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
        proposalContent: String,
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
    
    suspend fun updateProposalStatus(id: String, status: String, rejectionReason: String? = null): Proposal {
        val request = UpdateProposalStatusRequest(status = status, rejectionReason = rejectionReason)
        android.util.Log.d("ProposalRepository", "Updating status: id=$id, status=$status, reason=$rejectionReason")
        android.util.Log.d("ProposalRepository", "Request body: $request")
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



    suspend fun generateProposalWithAI(missionId: String): String {
        val aiRepository = com.example.matchify.data.remote.AiRepository(apiService.aiApi)
        val response = aiRepository.generateProposal(missionId)
        return response.proposalContent
    }

    fun generateProposalWithAIStream(missionId: String): kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.flow {
        android.util.Log.d("ProposalRepository", "🔵 [STREAMING] Starting proposal generation for mission: $missionId")
        
        // Get auth token
        val token = authPreferences.currentAccessToken.value
        if (token.isNullOrBlank()) {
            android.util.Log.e("ProposalRepository", " [STREAMING] No auth token found")
            return@flow
        }
        
        android.util.Log.d("ProposalRepository", " [STREAMING] Auth token found: ${token.take(20)}...")
        
        // Build SSE URL
        val baseUrl = "http://10.0.2.2:3000"
        val url = "$baseUrl/ai/proposals/generate/stream?missionId=$missionId"
        android.util.Log.d("ProposalRepository", " [STREAMING] SSE URL: $url")
        
        // Create SSE client
        val client = okhttp3.OkHttpClient.Builder()
            .readTimeout(5, java.util.concurrent.TimeUnit.MINUTES)
            .build()
        val sseClient = com.example.matchify.data.remote.sse.SSEClient(client)
        
        android.util.Log.d("ProposalRepository", " [STREAMING] SSE client created, connecting...")
        
        var eventCount = 0
        
        // Connect and process events
        sseClient.connect(url, token).collect { event ->
            eventCount++
            android.util.Log.d("ProposalRepository", " [STREAMING] Event #$eventCount received: ${event.data.take(100)}...")
            
            // Parse JSON data
            try {
                val json = org.json.JSONObject(event.data)
                
                // Check for error
                if (json.has("error") && json.getBoolean("error")) {
                    val message = if (json.has("message")) json.getString("message") else "Unknown error"
                    android.util.Log.e("ProposalRepository", " [STREAMING] SSE Error: $message")
                    return@collect
                }
                
                // Check for done marker
                if (json.has("done") && json.getBoolean("done")) {
                    android.util.Log.d("ProposalRepository", " [STREAMING] Stream complete (done marker received)")
                    return@collect
                }
                
                // Yield chunk
                if (json.has("chunk")) {
                    val chunk = json.getString("chunk")
                    android.util.Log.d("ProposalRepository", " [STREAMING] Yielding chunk: ${chunk.take(50)}...")
                    emit(chunk)
                } else {
                    android.util.Log.w("ProposalRepository", " [STREAMING] Event has no chunk field")
                }
            } catch (e: Exception) {
                android.util.Log.w("ProposalRepository", " [STREAMING] Could not parse JSON: ${e.message}")
            }
        }
        
        android.util.Log.d("ProposalRepository", " [STREAMING] SSE loop ended, total events: $eventCount")
    }
    
    /**
     * Filtrer les propositions avec IA et critères avancés
     */
    suspend fun filterProposals(
        request: com.example.matchify.data.remote.dto.proposal.ProposalFilterRequestDto
    ): Pair<List<Proposal>, com.example.matchify.data.remote.dto.proposal.ProposalFilterResponseDto> {
        val response = apiService.proposalApi.filterProposals(request)
        val proposals = response.proposals.map { ProposalDtoMapper.toDomain(it) }
        return Pair(proposals, response)
    }
    
    /**
     * Recalculer les scores IA pour les propositions d'une mission
     */
    suspend fun recalculateProposalScores(missionId: String): List<Proposal> {
        val response = apiService.proposalApi.recalculateProposalScores(missionId)
        return response.proposals.map { ProposalDtoMapper.toDomain(it) }
    }
}

