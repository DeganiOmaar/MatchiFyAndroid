package com.example.matchify.ui.talent.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.TalentFavoriteRepository

class FavoriteTalentsViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteTalentsViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val repository = TalentFavoriteRepository(apiService, authPreferences)

            return FavoriteTalentsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

