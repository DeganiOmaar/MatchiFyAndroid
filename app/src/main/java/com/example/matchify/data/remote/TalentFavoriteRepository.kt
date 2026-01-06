package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.profile.TalentUserDto
import com.example.matchify.data.remote.dto.profile.toDomain
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TalentFavoriteRepository(
    private val apiService: ApiService,
    private val authPreferences: AuthPreferences
) {
    
    suspend fun getFavoriteTalents(): List<UserModel> = withContext(Dispatchers.IO) {
        android.util.Log.d("TalentFavoriteRepository", "Fetching favorite talents")
        val talentDtos = apiService.talentFavoriteApi.getFavoriteTalents()
        android.util.Log.d("TalentFavoriteRepository", "Received ${talentDtos.size} favorite talents from API")
        val talents = talentDtos.map { it.toDomain() }
        android.util.Log.d("TalentFavoriteRepository", "Mapped to ${talents.size} UserModel objects")
        talents
    }
    
    suspend fun addFavoriteTalent(talentId: String) = withContext(Dispatchers.IO) {
        android.util.Log.d("TalentFavoriteRepository", "Adding talent $talentId to favorites")
        apiService.talentFavoriteApi.addFavoriteTalent(talentId)
        android.util.Log.d("TalentFavoriteRepository", "Successfully added talent $talentId to favorites")
    }
    
    suspend fun removeFavoriteTalent(talentId: String) = withContext(Dispatchers.IO) {
        android.util.Log.d("TalentFavoriteRepository", "Removing talent $talentId from favorites")
        apiService.talentFavoriteApi.removeFavoriteTalent(talentId)
        android.util.Log.d("TalentFavoriteRepository", "Successfully removed talent $talentId from favorites")
    }
}

