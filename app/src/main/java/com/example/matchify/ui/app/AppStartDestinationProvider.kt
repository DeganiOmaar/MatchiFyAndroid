package com.example.matchify.ui.app

import android.content.Context
import com.example.matchify.data.local.AuthPreferencesProvider

class AppStartDestinationProvider(private val context: Context) {

    suspend fun getStartDestination(): String {
        // Use the singleton instance of AuthPreferencesProvider
        val prefs = AuthPreferencesProvider.getInstance().get()

        val token = prefs.getTokenValue()
        val rememberMe = prefs.getRememberMeValue()
        val role = prefs.getRoleValue() // Get the stored role

        // If user is logged in (has token and remember me), go to home
        if (token != null && rememberMe) {
            // Check the role to determine the start destination
            return when (role) {
                "recruiter" -> "main"
                "talent" -> "home"
                else -> "login" // Default to login if role is unknown
            }
        }

        // User is not logged in → always show onboarding
        return "onboarding"
    }
}