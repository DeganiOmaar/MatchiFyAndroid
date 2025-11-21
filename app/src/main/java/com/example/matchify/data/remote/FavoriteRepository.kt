package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.favorite.FavoriteDto
import com.example.matchify.data.remote.dto.mission.MissionDto
import com.example.matchify.data.remote.dto.mission.MissionDtoMapper
import com.example.matchify.data.remote.dto.mission.toDomain
import com.example.matchify.domain.model.Mission

class FavoriteRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    
    suspend fun getFavorites(): List<Mission> {
        val missionDtos = apiService.favoriteApi.getFavorites()
        android.util.Log.d("FavoriteRepository", "Received ${missionDtos.size} favorite missions from API")
        val missions = missionDtos.map { missionDto ->
            missionDto.toDomain().copy(isFavorite = true)
        }
        android.util.Log.d("FavoriteRepository", "Mapped ${missions.size} missions, first mission title: ${missions.firstOrNull()?.title}")
        return missions
    }
    
    suspend fun addFavorite(missionId: String): FavoriteDto {
        return apiService.favoriteApi.addFavorite(missionId)
    }
    
    suspend fun removeFavorite(missionId: String) {
        apiService.favoriteApi.removeFavorite(missionId)
    }
}

