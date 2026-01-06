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
    
    /**
     * Filtrer les talents avec IA pour une mission
     * GET /ai/mission/{missionId}/candidates
     * 
     * @param missionId ID de la mission
     * @param page Numéro de page (optionnel)
     * @param limit Nombre de résultats par page (optionnel)
     * @param minScore Score minimum requis (optionnel)
     * @param experienceLevel Niveau d'expérience (optionnel)
     * @param location Localisation (optionnel)
     * @param skills Compétences requises, séparées par virgules (optionnel)
     */
    @GET("ai/mission/{missionId}/candidates")
    suspend fun getMissionCandidates(
        @Path("missionId") missionId: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("minScore") minScore: Int? = null,
        @Query("experienceLevel") experienceLevel: String? = null,
        @Query("location") location: String? = null,
        @Query("skills") skills: String? = null // Format: "skill1,skill2,skill3"
    ): com.example.matchify.data.remote.dto.ai.TalentFilterResponseDto
    
    /**
     * Filtrer les talents avec IA (POST avec body)
     * POST /ai/talents/filter
     * 
     * @param request Critères de filtrage
     */
    @POST("ai/talents/filter")
    suspend fun filterTalents(
        @Body request: com.example.matchify.data.remote.dto.ai.TalentFilterRequestDto
    ): com.example.matchify.data.remote.dto.ai.TalentFilterResponseDto
    
    /**
     * Récupérer les talents scorés pour une mission
     * GET /ai/mission/:missionId/talents?limit=20&minScore=60
     * 
     * @param missionId ID de la mission
     * @param limit Nombre max de talents à renvoyer (optionnel, défaut 50)
     * @param minScore Score minimum pour qu'un talent soit inclus (optionnel, défaut 0)
     * @return Liste de talents triés du meilleur au moins bon
     */
    @GET("ai/mission/{missionId}/talents")
    suspend fun getScoredTalentsForMission(
        @Path("missionId") missionId: String,
        @Query("limit") limit: Int? = null,
        @Query("minScore") minScore: Int? = null
    ): List<com.example.matchify.data.remote.dto.ai.TalentScoredDto>
}


