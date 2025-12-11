package com.example.matchify.ui.missions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.realtime.RealtimeManager

class MissionAddViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionAddViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val missionApi = ApiService.getInstance().missionApi
            val missionRepository = MissionRepository(missionApi, authPreferences)
            val realtimeManager = RealtimeManager.initialize(authPreferences)
            val realtimeClient = realtimeManager.missionClient

            return MissionAddViewModel(missionRepository, realtimeClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


