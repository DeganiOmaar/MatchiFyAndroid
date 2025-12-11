package com.example.matchify.data.local

import android.content.Context

// Change from object to a class that is explicitly initialized
class AuthPreferencesProvider private constructor(private val context: Context) {

    private lateinit var authPreferences: AuthPreferences

    fun get(): AuthPreferences {
        if (!this::authPreferences.isInitialized) {
            authPreferences = AuthPreferences(context)
        }
        return authPreferences
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthPreferencesProvider? = null

        fun initialize(context: Context) {
            // Ensure initialization happens only once
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = AuthPreferencesProvider(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): AuthPreferencesProvider {
            return INSTANCE ?: throw IllegalStateException("AuthPreferencesProvider not initialized. Call initialize() first.")
        }
    }
}