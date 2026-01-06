package com.example.matchify.data.remote.api

import com.example.matchify.domain.model.TalentRatingsResult
import retrofit2.http.GET
import retrofit2.http.Path

interface RatingApi {
    @GET("ratings/talent/{talentId}")
    suspend fun getTalentRatings(
        @Path("talentId") talentId: String,
        @retrofit2.http.Header("Authorization") token: String
    ): TalentRatingsResult
    
    @retrofit2.http.POST("ratings")
    suspend fun createRating(
        @retrofit2.http.Body request: com.example.matchify.data.remote.dto.rating.CreateRatingRequest,
        @retrofit2.http.Header("Authorization") token: String
    ): com.example.matchify.data.remote.dto.rating.RatingDto
    
    @retrofit2.http.DELETE("ratings/{ratingId}")
    suspend fun deleteRating(
        @Path("ratingId") ratingId: String,
        @retrofit2.http.Header("Authorization") token: String
    ): com.example.matchify.data.remote.dto.rating.RatingDto
}
