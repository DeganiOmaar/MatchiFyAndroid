package com.example.matchify.ui.auth.signup.talent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.AuthRepository

class TalentSignupViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentSignupViewModel::class.java)) {
            // Get singleton instances of dependencies
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val authApi = ApiService.getInstance().authApi
            val authRepository = AuthRepository(authApi)

            return TalentSignupViewModel(authRepository, authPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}