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

        // GLOBAL CRASH HANDLER: Show toast on crash
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("MatchiFyCrash", "Uncaught exception", throwable)
            
            // Try to show toast on main thread
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                try {
                    android.widget.Toast.makeText(
                        applicationContext,
                        "CRASH: ${throwable.localizedMessage}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    // Ignore toast error
                }
            }
            
            // Wait a bit before letting default handler kill app
            try { Thread.sleep(2000) } catch (e: InterruptedException) {}
            
            defaultHandler?.uncaughtException(thread, throwable)
        }

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