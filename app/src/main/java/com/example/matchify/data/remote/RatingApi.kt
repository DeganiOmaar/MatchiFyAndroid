package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.rating.CreateRatingRequest
import com.example.matchify.data.remote.dto.rating.RatingDto
import com.example.matchify.data.remote.dto.rating.TalentRatingsResponseDto
import retrofit2.http.*

/**
 * API pour les ratings
 */
interface RatingApi {
    
    /**
     * Créer ou mettre à jour un rating
     * POST /ratings
     * 
     * @param request Données du rating
     * @return Rating créé ou mis à jour
     */
    @POST("ratings")
    suspend fun createOrUpdateRating(@Body request: CreateRatingRequest): RatingDto
    
    /**
     * Récupérer le rating du recruteur courant
     * GET /ratings/my?talentId=<idTalent>&missionId=<idMissionOptionnel>
     * 
     * @param talentId ID du talent (obligatoire)
     * @param missionId ID de la mission (optionnel)
     * @return Rating ou null si aucun rating n'existe
     */
    @GET("ratings/my")
    suspend fun getMyRating(
        @Query("talentId") talentId: String,
        @Query("missionId") missionId: String? = null
    ): RatingDto?
    
    /**
     * Voir les feedbacks d'autres recruteurs pour un talent
     * GET /ratings/talent/:talentId
     * 
     * @param talentId ID du talent
     * @return Réponse avec moyenne, count et liste des ratings
     */
    @GET("ratings/talent/{talentId}")
    suspend fun getTalentRatings(@Path("talentId") talentId: String): TalentRatingsResponseDto
    
    /**
     * Supprimer un rating
     * DELETE /ratings/{ratingId}
     * 
     * @param ratingId ID du rating à supprimer
     */
    @DELETE("ratings/{ratingId}")
    suspend fun deleteRating(@Path("ratingId") ratingId: String): RatingDto
}

