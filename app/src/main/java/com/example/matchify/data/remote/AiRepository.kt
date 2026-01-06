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
    
    /**
     * Filtrer les talents avec IA pour une mission (GET)
     */
    suspend fun getMissionCandidates(
        missionId: String,
        page: Int? = null,
        limit: Int? = null,
        minScore: Int? = null,
        experienceLevel: String? = null,
        location: String? = null,
        skills: List<String>? = null
    ): TalentFilterResponseDto = withContext(Dispatchers.IO) {
        api.getMissionCandidates(
            missionId = missionId,
            page = page,
            limit = limit,
            minScore = minScore,
            experienceLevel = experienceLevel,
            location = location,
            skills = skills?.joinToString(",") // Convertir la liste en string séparée par virgules
        )
    }
    
    /**
     * Filtrer les talents avec IA (POST avec body)
     */
    suspend fun filterTalents(request: TalentFilterRequestDto): TalentFilterResponseDto = 
        withContext(Dispatchers.IO) {
            api.filterTalents(request)
        }
    
    /**
     * Récupérer les talents scorés pour une mission
     * GET /ai/mission/:missionId/talents?limit=20&minScore=60
     * 
     * @param missionId ID de la mission
     * @param limit Nombre max de talents à renvoyer (optionnel, défaut 50)
     * @param minScore Score minimum pour qu'un talent soit inclus (optionnel, défaut 0)
     * @return Liste de talents triés du meilleur au moins bon
     */
    suspend fun getScoredTalentsForMission(
        missionId: String,
        limit: Int? = null,
        minScore: Int? = null
    ): List<TalentScoredDto> = withContext(Dispatchers.IO) {
        api.getScoredTalentsForMission(
            missionId = missionId,
            limit = limit,
            minScore = minScore
        )
    }
}


