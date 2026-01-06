package com.example.matchify.ui.talent.filtering

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.UserRepository
import com.example.matchify.data.remote.dto.ai.TalentFilterRequestDto
import com.example.matchify.data.remote.dto.ai.toDomain
import com.example.matchify.domain.model.TalentCandidate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * ViewModel pour gérer le filtrage IA des talents
 */
class TalentFilteringViewModel(
    private val aiRepository: AiRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _candidates = MutableStateFlow<List<TalentCandidate>>(emptyList())
    val candidates: StateFlow<List<TalentCandidate>> = _candidates.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _totalResults = MutableStateFlow<Int?>(null)
    val totalResults: StateFlow<Int?> = _totalResults.asStateFlow()
    
    private val _currentPage = MutableStateFlow<Int?>(null)
    val currentPage: StateFlow<Int?> = _currentPage.asStateFlow()
    
    private val _currentMissionId = MutableStateFlow<String?>(null)
    val currentMissionId: StateFlow<String?> = _currentMissionId.asStateFlow()
    
    // Filtres actuels
    private val _filterMinScore = MutableStateFlow<Int?>(null)
    val filterMinScore: StateFlow<Int?> = _filterMinScore.asStateFlow()
    
    private val _filterExperienceLevel = MutableStateFlow<String?>(null)
    val filterExperienceLevel: StateFlow<String?> = _filterExperienceLevel.asStateFlow()
    
    private val _filterLocation = MutableStateFlow<String?>(null)
    val filterLocation: StateFlow<String?> = _filterLocation.asStateFlow()
    
    private val _filterSkills = MutableStateFlow<List<String>>(emptyList())
    val filterSkills: StateFlow<List<String>> = _filterSkills.asStateFlow()
    
    /**
     * Filtrer les talents pour une mission (GET)
     */
    fun filterTalentsForMission(
        missionId: String,
        page: Int? = null,
        limit: Int? = null,
        minScore: Int? = null,
        experienceLevel: String? = null,
        location: String? = null,
        skills: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _currentMissionId.value = missionId
            _filterMinScore.value = minScore
            _filterExperienceLevel.value = experienceLevel
            _filterLocation.value = location
            _filterSkills.value = skills ?: emptyList()
            
            try {
                val response = aiRepository.getMissionCandidates(
                    missionId = missionId,
                    page = page,
                    limit = limit,
                    minScore = minScore,
                    experienceLevel = experienceLevel,
                    location = location,
                    skills = skills
                )
                
                // Récupérer les détails complets des talents en parallèle
                val talentDetailsMap = fetchTalentDetails(response.candidates.map { it.talentId })
                
                // Convertir les DTOs en modèles domaine avec les détails
                val candidatesList = response.candidates.toDomain(talentDetailsMap)
                
                _candidates.value = candidatesList
                _totalResults.value = response.total
                _currentPage.value = response.page
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
     * Filtrer les talents avec critères avancés (POST)
     */
    fun filterTalents(request: TalentFilterRequestDto) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _currentMissionId.value = request.missionId
            
            try {
                val response = aiRepository.filterTalents(request)
                
                // Récupérer les détails complets des talents en parallèle
                val talentDetailsMap = fetchTalentDetails(response.candidates.map { it.talentId })
                
                // Convertir les DTOs en modèles domaine avec les détails
                val candidatesList = response.candidates.toDomain(talentDetailsMap)
                
                _candidates.value = candidatesList
                _totalResults.value = response.total
                _currentPage.value = response.page
                
                // Mettre à jour les filtres
                _filterMinScore.value = request.minScore
                _filterExperienceLevel.value = request.experienceLevel
                _filterLocation.value = request.location
                _filterSkills.value = request.skills ?: emptyList()
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
     * Récupérer les détails complets des talents en parallèle
     */
    private suspend fun fetchTalentDetails(talentIds: List<String>): Map<String, com.example.matchify.domain.model.UserModel> {
        return coroutineScope {
            talentIds.map { talentId ->
                async {
                    try {
                        val (user, _) = userRepository.getUserById(talentId) // Ignorer le portfolio pour l'instant
                        Pair(talentId, user)
                    } catch (e: Exception) {
                        android.util.Log.w("TalentFilteringViewModel", "Failed to fetch talent $talentId: ${e.message}")
                        Pair(talentId, null as com.example.matchify.domain.model.UserModel?)
                    }
                }
            }.mapNotNull { deferred ->
                val pair = deferred.await()
                if (pair.second != null) {
                    Pair(pair.first, pair.second!!)
                } else {
                    null
                }
            }.associate { it.first to it.second }
        }
    }
    
    /**
     * Réinitialiser les filtres
     */
    fun resetFilters() {
        _candidates.value = emptyList()
        _filterMinScore.value = null
        _filterExperienceLevel.value = null
        _filterLocation.value = null
        _filterSkills.value = emptyList()
        _errorMessage.value = null
        _totalResults.value = null
        _currentPage.value = null
        _currentMissionId.value = null
    }
    
    /**
     * Trier les candidats par score décroissant
     */
    fun sortByScoreDescending() {
        _candidates.value = _candidates.value.sortedByDescending { it.score }
    }
    
    /**
     * Trier les candidats par score croissant
     */
    fun sortByScoreAscending() {
        _candidates.value = _candidates.value.sortedBy { it.score }
    }
}

