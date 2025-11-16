package com.example.matchify

import android.app.Application
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize AuthPreferencesProvider first
        AuthPreferencesProvider.initialize(applicationContext)

        // 2. Get the singleton instance of AuthPreferences
        val authPreferences = AuthPreferencesProvider.getInstance().get()

        // 3. Initialize ApiService with AuthPreferences
        ApiService.initialize(authPreferences)
    }
}