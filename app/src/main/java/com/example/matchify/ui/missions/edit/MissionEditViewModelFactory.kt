package com.example.matchify.ui.missions.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.realtime.RealtimeManager
import com.example.matchify.domain.model.Mission

class MissionEditViewModelFactory(
    private val mission: Mission
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionEditViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val missionApi = ApiService.getInstance().missionApi
            val missionRepository = MissionRepository(missionApi, authPreferences)
            val realtimeManager = RealtimeManager.initialize(authPreferences)
            val realtimeClient = realtimeManager.missionClient

            return MissionEditViewModel(missionRepository, mission, realtimeClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


