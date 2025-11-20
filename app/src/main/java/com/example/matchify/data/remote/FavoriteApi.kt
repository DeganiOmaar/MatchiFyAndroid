package com.example.matchify.data.remote

import com.example.matchify.data.remote.dto.favorite.FavoriteDto
import com.example.matchify.data.remote.dto.mission.MissionDto
import retrofit2.http.*

interface FavoriteApi {
    
    @GET("favorites")
    suspend fun getFavorites(): List<MissionDto>
    
    @POST("favorites/{missionId}")
    suspend fun addFavorite(@Path("missionId") missionId: String): FavoriteDto
    
    @DELETE("favorites/{missionId}")
    suspend fun removeFavorite(@Path("missionId") missionId: String): Unit
}

