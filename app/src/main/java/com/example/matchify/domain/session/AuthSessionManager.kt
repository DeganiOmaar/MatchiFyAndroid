package com.example.matchify.domain.session

import android.util.Log
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.realtime.RealtimeManager

class AuthSessionManager private constructor(
    private val authPreferences: AuthPreferences,
    private val authRepository: AuthRepository,
    private val realtimeManager: RealtimeManager
) {

    companion object {
        @Volatile
        private var INSTANCE: AuthSessionManager? = null

        fun initialize(authPreferences: AuthPreferences) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        val authApi = ApiService.getInstance().authApi
                        val authRepository = AuthRepository(authApi)
                        val realtimeManager = RealtimeManager.getInstance()
                        INSTANCE = AuthSessionManager(authPreferences, authRepository, realtimeManager)
                    }
                }
            }
        }

        fun getInstance(): AuthSessionManager {
            return INSTANCE ?: throw IllegalStateException("AuthSessionManager has not been initialized")
        }
    }

    suspend fun logout() {
        // 1. Call backend logout endpoint
        val backendError = runCatching { authRepository.logout() }.exceptionOrNull()
        backendError?.let {
            Log.w("AuthSessionManager", "Backend logout failed, continuing local cleanup", it)
        }

        // 2. Disconnect realtime clients linked to the authenticated user
        runCatching { realtimeManager.disconnectAll() }
            .onFailure { Log.w("AuthSessionManager", "Failed to disconnect realtime clients", it) }

        // 3. Clear all persisted authentication data but keep onboarding preference
        runCatching { authPreferences.logout() }
            .onFailure { Log.e("AuthSessionManager", "Failed to clear local auth data", it) }
    }
}


