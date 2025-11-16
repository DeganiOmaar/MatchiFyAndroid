package com.example.matchify.ui.talent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.TalentRepository

class TalentProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        val repository = TalentRepository(apiService.talentApi, prefs)
        return TalentProfileViewModel(prefs, repository) as T
    }
}

