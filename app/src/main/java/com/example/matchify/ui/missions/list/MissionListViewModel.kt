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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

enum class MissionTab {
    BEST_MATCHES,
    MOST_RECENT
}

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
    
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    
    private val _selectedTab = MutableStateFlow(MissionTab.MOST_RECENT)
    val selectedTab: StateFlow<MissionTab> = _selectedTab
    
    private val _favoriteMissions = MutableStateFlow<Set<String>>(emptySet())
    val favoriteMissions: StateFlow<Set<String>> = _favoriteMissions
    
    private val _showProfileDrawer = MutableStateFlow(false)
    val showProfileDrawer: StateFlow<Boolean> = _showProfileDrawer

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
    
    fun updateSearchText(text: String) {
        _searchText.value = text
    }
    
    fun selectTab(tab: MissionTab) {
        _selectedTab.value = tab
    }
    
    fun toggleFavorite(mission: Mission) {
        _favoriteMissions.value = if (_favoriteMissions.value.contains(mission.missionId)) {
            _favoriteMissions.value - mission.missionId
        } else {
            _favoriteMissions.value + mission.missionId
        }
    }
    
    fun isFavorite(mission: Mission): Boolean {
        return _favoriteMissions.value.contains(mission.missionId)
    }
    
    fun openProfileDrawer() {
        _showProfileDrawer.value = true
    }
    
    fun closeProfileDrawer() {
        _showProfileDrawer.value = false
    }
    
    val filteredMissions: StateFlow<List<Mission>>
        get() = kotlinx.coroutines.flow.combine(
            _missions,
            _searchText,
            _selectedTab
        ) { missions, search, tab ->
            var filtered = missions
            
            // Apply search filter
            if (search.isNotEmpty()) {
                filtered = filtered.filter { mission ->
                    mission.title.contains(search, ignoreCase = true) ||
                    mission.description.contains(search, ignoreCase = true) ||
                    mission.skills.any { it.contains(search, ignoreCase = true) }
                }
            }
            
            // For now, both tabs show the same content (sorted by most recent)
            filtered.sortedByDescending { mission ->
                mission.createdAt?.let {
                    try {
                        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                        inputFormat.parse(it)?.time ?: 0L
                    } catch (e: Exception) {
                        0L
                    }
                } ?: 0L
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    override fun onCleared() {
        super.onCleared()
        // Don't disconnect - let RealtimeManager handle lifecycle
    }
}


