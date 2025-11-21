package com.example.matchify.ui.talent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.data.realtime.RealtimeManager

class TalentProfileViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        val repository = TalentRepository(apiService.talentApi, prefs)
        val portfolioRepository = PortfolioRepository(apiService.portfolioApi, prefs)
        val skillRepository = SkillRepository(apiService.skillApi)
        val realtimeManager = RealtimeManager.initialize(prefs)
        val realtimeClient = realtimeManager.profileClient
        return TalentProfileViewModel(prefs, repository, portfolioRepository, skillRepository, realtimeClient) as T
    }
}

