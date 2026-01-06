package com.example.matchify.data.remote

import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.dto.profile.toDomain
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val api: UserApi,
    private val prefs: AuthPreferences
) {
    
    suspend fun getUserById(id: String): Pair<UserModel, List<Project>> = withContext(Dispatchers.IO) {
        val currentUserId = prefs.currentUser.value?.id
        val response = api.getUserById(id)
        response.toDomain(currentUserId)
    }
    
    /**
     * Récupérer tous les talents enregistrés
     * Appelle GET /users/talents?limit={limit}&page={page}
     */
    suspend fun getAllTalents(limit: Int? = null, page: Int? = null): List<UserModel> = withContext(Dispatchers.IO) {
        android.util.Log.d("UserRepository", "📡 Calling GET /users/talents with limit=$limit, page=$page")
        try {
            val dtos = api.getAllTalents(limit = limit, page = page)
            android.util.Log.d("UserRepository", "✅ Received ${dtos.size} talents from API")
            dtos.map { dto ->
                UserModel(
                    id = dto.id,
                    fullName = dto.fullName,
                    email = dto.email,
                    role = dto.role,
                    phone = dto.phone,
                    profileImage = dto.profileImage,
                    location = dto.location,
                    talent = dto.talent,
                    description = dto.description,
                    skills = dto.skills,
                    cvUrl = dto.cvUrl,
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("UserRepository", "❌ Error in getAllTalents: ${e.message}", e)
            throw e
        }
    }
}

