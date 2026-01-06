package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.talent.TalentFilterRequestDto
import com.example.matchify.data.remote.dto.talent.toDomain
import com.example.matchify.domain.model.TalentMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository pour gérer le filtrage et scoring IA des talents
 */
class TalentMatchingRepository(
    private val api: TalentMatchingApi,
    private val prefs: AuthPreferences
) {
    
    /**
     * Récupérer les talents matchés pour une mission spécifique
     * 
     * @param missionId ID de la mission
     * @param minScore Score minimum requis (optionnel)
     * @param page Numéro de page pour la pagination (optionnel)
     * @param limit Nombre de résultats par page (optionnel)
     * @return Liste des talents matchés avec leurs scores
     */
    suspend fun getMatchedTalents(
        missionId: String? = null,
        minScore: Int? = null,
        page: Int? = null,
        limit: Int? = null
    ): List<TalentMatch> = withContext(Dispatchers.IO) {
        val response = api.getMatchedTalents(
            missionId = missionId,
            minScore = minScore,
            page = page,
            limit = limit
        )
        response.talents.toDomain()
    }
    
    /**
     * Filtrer les talents avec des critères avancés
     * 
     * @param request Critères de filtrage (skills, location, experienceLevel, etc.)
     * @return Liste des talents filtrés avec leurs scores
     */
    suspend fun filterTalents(request: TalentFilterRequestDto): List<TalentMatch> = 
        withContext(Dispatchers.IO) {
            val response = api.filterTalents(request)
            response.talents.toDomain()
        }
    
    /**
     * Calculer le score de matching pour un talent spécifique avec une mission
     * 
     * @param talentId ID du talent
     * @param missionId ID de la mission
     * @return Talent avec score de matching calculé
     */
    suspend fun calculateMatchScore(
        talentId: String,
        missionId: String
    ): TalentMatch? = withContext(Dispatchers.IO) {
        val response = api.calculateMatchScore(talentId, missionId)
        response.talents.firstOrNull()?.toDomain()
    }
}

