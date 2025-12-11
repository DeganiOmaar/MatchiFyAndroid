package com.example.matchify

import android.app.Application
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.realtime.RealtimeManager
import com.example.matchify.domain.session.AuthSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize AuthPreferencesProvider first
        AuthPreferencesProvider.initialize(applicationContext)

        // 2. Get the singleton instance of AuthPreferences
        val authPreferences = AuthPreferencesProvider.getInstance().get()

        // 3. Initialize ApiService with AuthPreferences
        ApiService.initialize(authPreferences)

        // 4. Initialize RealtimeManager
        RealtimeManager.initialize(authPreferences)

        // 5. Initialize AuthSessionManager so logout flow mirrors iOS
        AuthSessionManager.initialize(authPreferences)

        // 6. Connect realtime clients if user is logged in
        applicationScope.launch {
            val token = authPreferences.currentAccessToken.value
            if (!token.isNullOrBlank()) {
                val realtimeManager = RealtimeManager.getInstance()
                realtimeManager.connectAll()
            }
        }
    }
}