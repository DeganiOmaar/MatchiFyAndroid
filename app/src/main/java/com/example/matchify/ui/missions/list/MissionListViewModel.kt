package com.example.matchify.ui.missions.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.FavoriteRepository
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
    MOST_RECENT,
    FAVORITES
}

class MissionListViewModel(
    private val repository: MissionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val realtimeClient: MissionRealtimeClient,
    private val authPreferences: com.example.matchify.data.local.AuthPreferences
) : ViewModel() {

    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions

    private val _favoriteMissionsList = MutableStateFlow<List<Mission>>(emptyList())
    val favoriteMissionsList: StateFlow<List<Mission>> = _favoriteMissionsList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites: StateFlow<Boolean> = _isLoadingFavorites

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText
    
    private val _selectedTab = MutableStateFlow(MissionTab.MOST_RECENT)
    val selectedTab: StateFlow<MissionTab> = _selectedTab
    
    private val _favoriteMissionsIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteMissionsIds: StateFlow<Set<String>> = _favoriteMissionsIds
    
    val isTalent: Boolean
        get() = authPreferences.currentRole.value == "talent"
    
    private val _showProfileDrawer = MutableStateFlow(false)
    val showProfileDrawer: StateFlow<Boolean> = _showProfileDrawer
    
    private val _drawerNavigationItem = MutableStateFlow<com.example.matchify.ui.missions.components.DrawerMenuItemType?>(null)
    val drawerNavigationItem: StateFlow<com.example.matchify.ui.missions.components.DrawerMenuItemType?> = _drawerNavigationItem
    
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
                
                // Update favorite IDs from missions that have isFavorite = true
                if (isTalent) {
                    val favoriteIds = allMissions
                        .filter { it.isFavorite == true }
                        .mapNotNull { it.missionId.takeIf { id -> id.isNotEmpty() } }
                        .toSet()
                    _favoriteMissionsIds.value = _favoriteMissionsIds.value + favoriteIds
                }
                
                _isLoading.value = false
                
                // Load favorites if talent
                if (isTalent) {
                    loadFavorites()
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            }
        }
    }
    
    fun loadFavorites() {
        if (!isTalent) {
            android.util.Log.d("MissionListViewModel", "loadFavorites: User is not a talent, skipping")
            return
        }
        
        android.util.Log.d("MissionListViewModel", "loadFavorites: Starting to load favorites")
        _isLoadingFavorites.value = true
        viewModelScope.launch {
            try {
                val favorites = favoriteRepository.getFavorites()
                android.util.Log.d("MissionListViewModel", "loadFavorites: Received ${favorites.size} favorites from repository")
                _favoriteMissionsList.value = favorites
                
                // Update favorite IDs from the favorites list
                val favoriteIds = favorites.mapNotNull { mission -> 
                    mission.missionId.takeIf { it.isNotEmpty() } 
                }.toSet()
                android.util.Log.d("MissionListViewModel", "loadFavorites: Updated favorite IDs: ${favoriteIds.size} IDs")
                _favoriteMissionsIds.value = favoriteIds
                
                // Also update missions list to mark favorites as favorite
                _missions.value = _missions.value.map { mission ->
                    if (favoriteIds.contains(mission.missionId)) {
                        mission.copy(isFavorite = true)
                    } else {
                        mission
                    }
                }
                
                android.util.Log.d("MissionListViewModel", "loadFavorites: Successfully loaded ${favorites.size} favorites")
                _isLoadingFavorites.value = false
            } catch (e: Exception) {
                android.util.Log.e("MissionListViewModel", "loadFavorites: Error loading favorites", e)
                _isLoadingFavorites.value = false
                _errorMessage.value = "Failed to load favorites: ${e.message}"
                // Don't silently fail - show error to user
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
        val wasFavorite = isFavorite(mission)
        val newFavoriteState = !wasFavorite
        
        // Optimistic update - update local state immediately
        if (wasFavorite) {
            _favoriteMissionsIds.value = _favoriteMissionsIds.value - mission.missionId
            _favoriteMissionsList.value = _favoriteMissionsList.value.filter { it.missionId != mission.missionId }
        } else {
            _favoriteMissionsIds.value = _favoriteMissionsIds.value + mission.missionId
            // Add mission to favorites list if not already there
            if (!_favoriteMissionsList.value.any { it.missionId == mission.missionId }) {
                _favoriteMissionsList.value = _favoriteMissionsList.value + mission.copy(isFavorite = true)
            }
        }
        
        // Update mission in missions list with new favorite status
        _missions.value = _missions.value.map { m ->
            if (m.missionId == mission.missionId) {
                m.copy(isFavorite = newFavoriteState)
            } else {
                m
            }
        }
        
        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    favoriteRepository.removeFavorite(mission.missionId)
                } else {
                    favoriteRepository.addFavorite(mission.missionId)
                }
                
                // Reload favorites list if on Favorites tab to ensure sync
                if (selectedTab.value == MissionTab.FAVORITES) {
                    loadFavorites()
                }
                // Note: We don't reload all missions here to avoid unnecessary API calls
                // The optimistic update already reflects the change in the UI
            } catch (e: Exception) {
                // Revert on error
                if (wasFavorite) {
                    _favoriteMissionsIds.value = _favoriteMissionsIds.value + mission.missionId
                    if (!_favoriteMissionsList.value.any { it.missionId == mission.missionId }) {
                        _favoriteMissionsList.value = _favoriteMissionsList.value + mission
                    }
                } else {
                    _favoriteMissionsIds.value = _favoriteMissionsIds.value - mission.missionId
                    _favoriteMissionsList.value = _favoriteMissionsList.value.filter { it.missionId != mission.missionId }
                }
                
                // Revert mission favorite status
                _missions.value = _missions.value.map { m ->
                    if (m.missionId == mission.missionId) {
                        m.copy(isFavorite = wasFavorite)
                    } else {
                        m
                    }
                }
                
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            }
        }
    }
    
    fun isFavorite(mission: Mission): Boolean {
        // Check both the local favorite IDs set and the mission's isFavorite property from backend
        return _favoriteMissionsIds.value.contains(mission.missionId) || mission.isFavorite == true
    }
    
    fun openProfileDrawer() {
        _showProfileDrawer.value = true
    }
    
    fun closeProfileDrawer() {
        _showProfileDrawer.value = false
    }
    
    fun setDrawerNavigationItem(itemType: com.example.matchify.ui.missions.components.DrawerMenuItemType) {
        _drawerNavigationItem.value = itemType
    }
    
    fun clearDrawerNavigationItem() {
        _drawerNavigationItem.value = null
    }
    
    val filteredMissions: StateFlow<List<Mission>>
        get() = kotlinx.coroutines.flow.combine(
            _missions,
            _favoriteMissionsList,
            _searchText,
            _selectedTab
        ) { missions, favorites, search, tab ->
            var filtered = when (tab) {
                MissionTab.FAVORITES -> favorites
                MissionTab.BEST_MATCHES, MissionTab.MOST_RECENT -> missions
            }
            
            // Apply search filter
            if (search.isNotEmpty()) {
                filtered = filtered.filter { mission ->
                    mission.title.contains(search, ignoreCase = true) ||
                    mission.description.contains(search, ignoreCase = true) ||
                    mission.skills.any { it.contains(search, ignoreCase = true) }
                }
            }
            
            // Sort based on tab
            when (tab) {
                MissionTab.BEST_MATCHES, MissionTab.FAVORITES -> {
                    // Sort by most recent
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
                }
                MissionTab.MOST_RECENT -> {
                    // Sort by most recent
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
                }
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


