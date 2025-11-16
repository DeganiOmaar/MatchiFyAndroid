package com.example.matchify.ui.missions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository

class MissionAddViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionAddViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val missionApi = ApiService.getInstance().missionApi
            val missionRepository = MissionRepository(missionApi, authPreferences)

            return MissionAddViewModel(missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


