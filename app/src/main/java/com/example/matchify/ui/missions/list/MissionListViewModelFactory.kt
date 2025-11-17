package com.example.matchify.ui.missions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.realtime.MissionRealtimeClient

class MissionListViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionListViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val missionApi = ApiService.getInstance().missionApi
            val missionRepository = MissionRepository(missionApi, authPreferences)
            val realtimeClient = MissionRealtimeClient(authPreferences)

            return MissionListViewModel(missionRepository, realtimeClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


