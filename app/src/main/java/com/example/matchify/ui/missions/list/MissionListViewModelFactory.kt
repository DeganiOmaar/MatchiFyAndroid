package com.example.matchify.ui.missions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.FavoriteRepository
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.realtime.MissionRealtimeClient
import com.example.matchify.data.realtime.RealtimeManager

class MissionListViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionListViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val missionRepository = MissionRepository(apiService.missionApi, authPreferences)
            val favoriteRepository = FavoriteRepository(apiService, authPreferences)
            val realtimeManager = RealtimeManager.initialize(authPreferences)
            val realtimeClient = realtimeManager.missionClient

            val talentRepository = com.example.matchify.data.remote.TalentRepository(apiService.talentApi, authPreferences)

            return MissionListViewModel(missionRepository, favoriteRepository, realtimeClient, authPreferences, talentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


