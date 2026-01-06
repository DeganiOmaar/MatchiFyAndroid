package com.example.matchify.ui.talent.filtering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.UserRepository

class TalentFilteringViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentFilteringViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val aiRepository = AiRepository(apiService.aiApi)
            val userRepository = UserRepository(apiService.userApi, prefs)
            return TalentFilteringViewModel(aiRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

