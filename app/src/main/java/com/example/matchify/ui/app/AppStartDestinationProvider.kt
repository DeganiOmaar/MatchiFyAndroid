package com.example.matchify.ui.app

import android.content.Context
import com.example.matchify.data.local.AuthPreferencesProvider

class AppStartDestinationProvider(private val context: Context) {

    suspend fun getStartDestination(): String {
        // Use the singleton instance of AuthPreferencesProvider
        val prefs = AuthPreferencesProvider.getInstance().get()

        val token = prefs.getTokenValue()
        val rememberMe = prefs.getRememberMeValue()
        val hasSeenOnboarding = prefs.getHasSeenOnboardingValue()

        // If user is logged in (has token and remember me), go to main
        if (token != null && rememberMe) {
            // Both Talent and Recruiter go to "main" (Missions + Profile with bottom nav)
            return "main"
        }

        // User is not logged in
        // Show onboarding only if they haven't seen it before
        // After logout, hasSeenOnboarding is preserved, so they go to login
        return if (hasSeenOnboarding) {
            "login"
        } else {
            "onboarding"
        }
    }
}