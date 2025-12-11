package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.ai.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository pour les fonctionnalités AI
 */
class AiRepository(
    private val api: AiApi
) {
    
    /**
     * Analyser le profil avec AI (Ollama)
     */
    suspend fun analyzeProfile(): ProfileAnalysisResponseDto = withContext(Dispatchers.IO) {
        api.analyzeProfile()
    }
    
    /**
     * Récupérer la dernière analyse du profil
     */
    suspend fun getLatestProfileAnalysis(): ProfileAnalysisResponseDto = withContext(Dispatchers.IO) {
        api.getLatestProfileAnalysis()
    }
    
    /**
     * Obtenir l'analyse de compatibilité mission-profil
     */
    suspend fun analyzeMissionFit(missionId: String): MissionFitResponseDto = withContext(Dispatchers.IO) {
        api.analyzeMissionFit(missionId)
    }
    
    /**
     * Générer une proposition avec AI
     */
    suspend fun generateProposal(missionId: String): GenerateProposalResponseDto = withContext(Dispatchers.IO) {
        api.generateProposal(GenerateProposalRequestDto(missionId))
    }
}


