package com.example.matchify.ui.missions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MissionDetailsViewModel(
    private val missionId: String,
    private val repository: MissionRepository
) : ViewModel() {
    
    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val shouldShowApplyButton: StateFlow<Boolean> = MutableStateFlow(false).also { flow ->
        viewModelScope.launch {
            val prefs = AuthPreferencesProvider.getInstance().get()
            flow.value = prefs.currentRole.value == "talent"
        }
    }
    
    val canApply: StateFlow<Boolean> = MutableStateFlow(false).also { flow ->
        viewModelScope.launch {
            _mission.collect { mission ->
                flow.value = mission?.let { !it.hasAppliedToMission } ?: false
            }
        }
    }
    
    init {
        loadMission()
    }
    
    fun loadMission() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val mission = repository.getMissionById(missionId)
                _mission.value = mission
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement de la mission"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class MissionDetailsViewModelFactory(
    private val missionId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionDetailsViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val api = apiService.missionApi
            val repository = MissionRepository(api, prefs)
            @Suppress("UNCHECKED_CAST")
            return MissionDetailsViewModel(missionId, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

