package com.example.matchify.ui.talent.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.TalentMatchingRepository
import com.example.matchify.data.remote.dto.talent.TalentFilterRequestDto
import com.example.matchify.domain.model.TalentMatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel pour gérer le filtrage et scoring IA des talents
 */
class TalentMatchingViewModel(
    private val repository: TalentMatchingRepository
) : ViewModel() {
    
    private val _talents = MutableStateFlow<List<TalentMatch>>(emptyList())
    val talents: StateFlow<List<TalentMatch>> = _talents.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _totalResults = MutableStateFlow<Int?>(null)
    val totalResults: StateFlow<Int?> = _totalResults.asStateFlow()
    
    // Filtres actuels
    private val _currentMissionId = MutableStateFlow<String?>(null)
    val currentMissionId: StateFlow<String?> = _currentMissionId.asStateFlow()
    
    private val _currentMinScore = MutableStateFlow<Int?>(null)
    val currentMinScore: StateFlow<Int?> = _currentMinScore.asStateFlow()
    
    /**
     * Charger les talents matchés pour une mission
     */
    fun loadMatchedTalentsForMission(
        missionId: String,
        minScore: Int? = null,
        page: Int? = null,
        limit: Int? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _currentMissionId.value = missionId
            _currentMinScore.value = minScore
            
            try {
                val matchedTalents = repository.getMatchedTalents(
                    missionId = missionId,
                    minScore = minScore,
                    page = page,
                    limit = limit
                )
                _talents.value = matchedTalents
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(
                    e,
                    ErrorContext.TALENT_MATCHING
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Filtrer les talents avec des critères avancés
     */
    fun filterTalents(
        missionId: String? = null,
        skills: List<String>? = null,
        minScore: Int? = null,
        location: String? = null,
        experienceLevel: String? = null,
        page: Int? = null,
        limit: Int? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val request = TalentFilterRequestDto(
                    missionId = missionId,
                    skills = skills,
                    minScore = minScore,
                    location = location,
                    experienceLevel = experienceLevel,
                    page = page,
                    limit = limit
                )
                
                val filteredTalents = repository.filterTalents(request)
                _talents.value = filteredTalents
                
                // Mettre à jour les filtres actuels
                _currentMissionId.value = missionId
                _currentMinScore.value = minScore
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(
                    e,
                    ErrorContext.TALENT_MATCHING
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Calculer le score de matching pour un talent spécifique
     */
    fun calculateMatchScore(talentId: String, missionId: String) {
        viewModelScope.launch {
            try {
                val talentWithScore = repository.calculateMatchScore(talentId, missionId)
                if (talentWithScore != null) {
                    // Mettre à jour le talent dans la liste
                    _talents.value = _talents.value.map { talent ->
                        if (talent.talentId == talentId) {
                            talentWithScore
                        } else {
                            talent
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(
                    e,
                    ErrorContext.TALENT_MATCHING
                )
            }
        }
    }
    
    /**
     * Réinitialiser les filtres et la liste
     */
    fun reset() {
        _talents.value = emptyList()
        _currentMissionId.value = null
        _currentMinScore.value = null
        _errorMessage.value = null
        _totalResults.value = null
    }
    
    /**
     * Trier les talents par score décroissant
     */
    fun sortByScoreDescending() {
        _talents.value = _talents.value.sortedByDescending { it.matchScore }
    }
    
    /**
     * Trier les talents par score croissant
     */
    fun sortByScoreAscending() {
        _talents.value = _talents.value.sortedBy { it.matchScore }
    }
    
    /**
     * Filtrer les talents par score minimum
     */
    fun filterByMinScore(minScore: Int) {
        _talents.value = _talents.value.filter { it.matchScore >= minScore }
    }
}

