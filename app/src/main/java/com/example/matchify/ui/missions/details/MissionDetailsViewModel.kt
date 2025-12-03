package com.example.matchify.ui.missions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.FavoriteRepository
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MissionDetailsViewModel(
    private val missionId: String,
    private val repository: MissionRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    
    private val _mission = MutableStateFlow<Mission?>(null)
    val mission: StateFlow<Mission?> = _mission.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isTogglingFavorite = MutableStateFlow(false)
    val isTogglingFavorite: StateFlow<Boolean> = _isTogglingFavorite.asStateFlow()
    
    val isTalent: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "talent"
        }
    
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
    
    fun toggleFavorite() {
        val currentMission = _mission.value ?: return
        viewModelScope.launch {
            _isTogglingFavorite.value = true
            try {
                if (currentMission.isFavorited) {
                    favoriteRepository.removeFavorite(currentMission.missionId)
                    _mission.value = currentMission.copy(isFavorite = false)
                } else {
                    favoriteRepository.addFavorite(currentMission.missionId)
                    _mission.value = currentMission.copy(isFavorite = true)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de la mise à jour des favoris"
            } finally {
                _isTogglingFavorite.value = false
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
            val favoriteRepository = FavoriteRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return MissionDetailsViewModel(missionId, repository, favoriteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

