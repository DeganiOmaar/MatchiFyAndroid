package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.profile.TalentUserDto
import retrofit2.http.*

interface TalentFavoriteApi {
    
    @GET("recruiter-favorites/talents")
    suspend fun getFavoriteTalents(): List<TalentUserDto>
    
    @POST("recruiter-favorites/talent/{talentId}")
    suspend fun addFavoriteTalent(@Path("talentId") talentId: String): Unit
    
    @DELETE("recruiter-favorites/talent/{talentId}")
    suspend fun removeFavoriteTalent(@Path("talentId") talentId: String): Unit
}

