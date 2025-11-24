package com.example.matchify.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.domain.session.AuthSessionManager

class SettingsViewModelFactory(
    private val authPreferences: AuthPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val sessionManager = AuthSessionManager.getInstance()
            return SettingsViewModel(authPreferences, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

