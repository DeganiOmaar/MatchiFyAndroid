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
}

