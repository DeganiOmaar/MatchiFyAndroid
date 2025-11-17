package com.example.matchify.ui.missions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.realtime.MissionRealtimeClient
import com.example.matchify.data.realtime.MissionRealtimeEvent
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MissionListViewModel(
    private val repository: MissionRepository,
    private val realtimeClient: MissionRealtimeClient
) : ViewModel() {

    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadMissions()
        observeRealtimeUpdates()
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
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            }
        }
    }

    fun refreshMissions() {
        loadMissions()
    }

    private fun observeRealtimeUpdates() {
        realtimeClient.connect()
        viewModelScope.launch {
            realtimeClient.events.collect { event ->
                when (event) {
                    is MissionRealtimeEvent.MissionCreated -> {
                        _missions.update { current ->
                            listOf(event.mission) + current.filterNot { it.missionId == event.mission.missionId }
                        }
                    }
                    is MissionRealtimeEvent.MissionUpdated -> {
                        _missions.update { current ->
                            current.map { mission ->
                                if (mission.missionId == event.mission.missionId) {
                                    event.mission
                                } else {
                                    mission
                                }
                            }
                        }
                    }
                    is MissionRealtimeEvent.MissionDeleted -> {
                        _missions.update { current ->
                            current.filterNot { it.missionId == event.missionId }
                        }
                    }
                }
            }
        }
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch {
            try {
                repository.deleteMission(mission.missionId)
                // Remove from local list
                _missions.value = _missions.value.filter { it.missionId != mission.missionId }
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.MISSION_DELETE)
            }
        }
    }

    fun isMissionOwner(mission: Mission): Boolean {
        return repository.isMissionOwner(mission)
    }

    override fun onCleared() {
        super.onCleared()
        realtimeClient.disconnect()
    }
}


