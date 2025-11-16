package com.example.matchify.ui.recruiter.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RecruiterRepository

class RecruiterProfileViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecruiterProfileViewModel::class.java)) {
            // Get singleton instances of dependencies
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val recruiterApi = ApiService.getInstance().recruiterApi
            val recruiterRepository = RecruiterRepository(recruiterApi, authPreferences)

            return RecruiterProfileViewModel(authPreferences, recruiterRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}