package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.talent.TalentFilterRequestDto
import com.example.matchify.data.remote.dto.talent.TalentMatchResponseDto
import retrofit2.http.*

/**
 * API pour le filtrage et scoring IA des talents
 * Utilisée par les recruteurs pour trouver les meilleurs talents
 */
interface TalentMatchingApi {
    
    /**
     * Filtrer et scorer les talents pour une mission spécifique
     * GET /talents/match?missionId={missionId}
     * 
     * @param missionId ID de la mission pour laquelle filtrer les talents
     * @param minScore Score minimum (optionnel)
     * @param page Numéro de page (optionnel)
     * @param limit Nombre de résultats par page (optionnel)
     */
    @GET("talents/match")
    suspend fun getMatchedTalents(
        @Query("missionId") missionId: String? = null,
        @Query("minScore") minScore: Int? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): TalentMatchResponseDto
    
    /**
     * Filtrer les talents avec des critères avancés
     * POST /talents/filter
     * 
     * @param request Critères de filtrage (skills, location, experienceLevel, etc.)
     */
    @POST("talents/filter")
    suspend fun filterTalents(
        @Body request: TalentFilterRequestDto
    ): TalentMatchResponseDto
    
    /**
     * Calculer le score de matching pour un talent spécifique avec une mission
     * POST /talents/{talentId}/match-score
     * 
     * @param talentId ID du talent
     * @param missionId ID de la mission
     */
    @POST("talents/{talentId}/match-score")
    suspend fun calculateMatchScore(
        @Path("talentId") talentId: String,
        @Query("missionId") missionId: String
    ): TalentMatchResponseDto
}

