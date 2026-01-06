package com.example.matchify.data.remote.api

import com.example.matchify.domain.model.TalentRatingsResult
import retrofit2.http.GET
import retrofit2.http.Path

interface RatingApi {
    @GET("ratings/talent/{talentId}")
    suspend fun getTalentRatings(@Path("talentId") talentId: String): TalentRatingsResult
}
