package com.example.matchify.ui.missions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MissionListViewModel(
    private val repository: MissionRepository
) : ViewModel() {

    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadMissions()
    }

    fun loadMissions() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val allMissions = repository.getAllMissions()
                _missions.value = allMissions
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Failed to load missions"
            }
        }
    }

    fun refreshMissions() {
        loadMissions()
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch {
            try {
                repository.deleteMission(mission.missionId)
                // Remove from local list
                _missions.value = _missions.value.filter { it.missionId != mission.missionId }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete mission"
            }
        }
    }

    fun isMissionOwner(mission: Mission): Boolean {
        return repository.isMissionOwner(mission)
    }
}

