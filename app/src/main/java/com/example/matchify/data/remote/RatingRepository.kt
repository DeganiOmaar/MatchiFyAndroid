package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.rating.CreateRatingRequest
import com.example.matchify.data.remote.dto.rating.toDomain
import com.example.matchify.domain.model.Rating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository pour les ratings
 */
class RatingRepository(
    private val api: RatingApi
) {
    
    /**
     * Créer ou mettre à jour un rating
     * 
     * Note: recommended et tags ont été supprimés du schéma backend
     */
    suspend fun createOrUpdateRating(
        talentId: String,
        missionId: String? = null,
        score: Int,
        recommended: Boolean,
        comment: String? = null
    ): Rating = withContext(Dispatchers.IO) {
        val request = CreateRatingRequest(
            talentId = talentId,
            missionId = missionId,
            score = score,
            recommended = recommended,
            comment = comment
        )
        
        // Log pour déboguer
        android.util.Log.d("RatingRepository", "Creating rating request: talentId=$talentId, score=$score, comment=$comment")
        
        api.createOrUpdateRating(request).toDomain()
    }
    
    /**
     * Récupérer le rating du recruteur courant
     */
    suspend fun getMyRating(
        talentId: String,
        missionId: String? = null
    ): Rating? = withContext(Dispatchers.IO) {
        api.getMyRating(talentId, missionId)?.toDomain()
    }
    
    /**
     * Voir les feedbacks d'autres recruteurs pour un talent
     */
    suspend fun getTalentRatings(talentId: String): TalentRatingsResponse = withContext(Dispatchers.IO) {
        val response = api.getTalentRatings(talentId)
        TalentRatingsResponse(
            talentId = response.talentId,
            averageScore = response.averageScore,
            count = response.count,
            bayesianScore = response.bayesianScore,
            ratings = response.ratings.map { it.toDomain() }
        )
    }
    
    /**
     * Supprimer un rating
     */
    suspend fun deleteRating(ratingId: String): Rating = withContext(Dispatchers.IO) {
        api.deleteRating(ratingId).toDomain()
    }
}

/**
 * Réponse pour les ratings d'un talent
 */
data class TalentRatingsResponse(
    val talentId: String,
    val averageScore: Double?,
    val count: Int,
    val bayesianScore: Double?, // score bayésien pondéré
    val ratings: List<Rating>
)

