package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.ai.*
import retrofit2.http.*

/**
 * API pour les fonctionnalités AI
 * Réutilise les mêmes endpoints que iOS
 */
interface AiApi {
    
    /**
     * Analyse du profil avec AI (Ollama)
     * POST /ai/profile-analysis
     * Body: {} (vide)
     */
    @POST("ai/profile-analysis")
    suspend fun analyzeProfile(): ProfileAnalysisResponseDto
    
    /**
     * Récupérer la dernière analyse du profil
     * GET /ai/profile-analysis
     */
    @GET("ai/profile-analysis")
    suspend fun getLatestProfileAnalysis(): ProfileAnalysisResponseDto
    
    /**
     * Analyse de compatibilité mission-profil
     * POST /ai/mission-fit/{missionId}
     * Body: {} (vide, comme iOS)
     */
    @POST("ai/mission-fit/{missionId}")
    suspend fun analyzeMissionFit(@Path("missionId") missionId: String): MissionFitResponseDto
    
    /**
     * Générer une proposition avec AI
     * POST /ai/proposals/generate
     */
    @POST("ai/proposals/generate")
    suspend fun generateProposal(@Body request: GenerateProposalRequestDto): GenerateProposalResponseDto
}


