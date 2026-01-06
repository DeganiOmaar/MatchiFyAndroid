package com.example.matchify.ui.proposals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ProposalRepository
import com.example.matchify.domain.model.Mission
import com.example.matchify.domain.model.Proposal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ProposalStatusFilter(val displayName: String, val apiValue: String?) {
    ALL("All", null),
    ACCEPTED("Accepted", "ACCEPTED"),
    REFUSED("Refused", "REFUSED"),
    VIEWED("Viewed", "VIEWED"),
    NOT_VIEWED("Not Viewed", "NOT_VIEWED")
}

enum class ProposalTab(val displayName: String) {
    ACTIVE("Active"),
    ARCHIVE("Archive")
}

class ProposalsViewModel(
    private val repository: ProposalRepository
) : ViewModel() {
    
    private val _proposals = MutableStateFlow<List<Proposal>>(emptyList())
    val proposals: StateFlow<List<Proposal>> = _proposals.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _selectedStatusFilter = MutableStateFlow(ProposalStatusFilter.ALL)
    val selectedStatusFilter: StateFlow<ProposalStatusFilter> = _selectedStatusFilter.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(ProposalTab.ACTIVE)
    val selectedTab: StateFlow<ProposalTab> = _selectedTab.asStateFlow()
    
    // Recruiter-specific state
    private val _missions = MutableStateFlow<List<Mission>>(emptyList())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()
    
    private val _selectedMission = MutableStateFlow<Mission?>(null)
    val selectedMission: StateFlow<Mission?> = _selectedMission.asStateFlow()
    
    private val _aiSortEnabled = MutableStateFlow(false)
    val aiSortEnabled: StateFlow<Boolean> = _aiSortEnabled.asStateFlow()
    
    private val _isLoadingMissions = MutableStateFlow(false)
    val isLoadingMissions: StateFlow<Boolean> = _isLoadingMissions.asStateFlow()
    
    // Filtrage IA
    private val _filterMinScore = MutableStateFlow<Int?>(null)
    val filterMinScore: StateFlow<Int?> = _filterMinScore.asStateFlow()
    
    private val _filterSkills = MutableStateFlow<List<String>>(emptyList())
    val filterSkills: StateFlow<List<String>> = _filterSkills.asStateFlow()
    
    private val _averageScore = MutableStateFlow<Double?>(null)
    val averageScore: StateFlow<Double?> = _averageScore.asStateFlow()
    
    // Meilleures propositions (top 1-2)
    private val _topProposals = MutableStateFlow<List<Proposal>>(emptyList())
    val topProposals: StateFlow<List<Proposal>> = _topProposals.asStateFlow()
    
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    init {
        if (isRecruiter) {
            loadMissions()
        } else {
            loadProposals()
        }
    }
    
    fun selectTab(tab: ProposalTab) {
        _selectedTab.value = tab
        // Reset status filter when switching to Archive (status filters don't apply to Archive)
        if (tab == ProposalTab.ARCHIVE) {
            _selectedStatusFilter.value = ProposalStatusFilter.ALL
        }
        loadProposals()
    }
    
    fun selectStatusFilter(filter: ProposalStatusFilter) {
        _selectedStatusFilter.value = filter
        loadProposals()
    }
    
    fun loadProposals() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val proposalsList = if (isRecruiter) {
                    // Recruiter: load proposals for selected mission with optional AI sort
                    val selectedMissionId = _selectedMission.value?.missionId
                    if (selectedMissionId != null) {
                        val (_, proposals) = repository.getProposalsForMission(
                            missionId = selectedMissionId,
                            aiSort = _aiSortEnabled.value
                        )
                        proposals
                    } else {
                        emptyList()
                    }
                } else {
                    val archived = if (_selectedTab.value == ProposalTab.ARCHIVE) true else null
                    // Only apply status filter for Active tab
                    val statusFilter = if (_selectedTab.value == ProposalTab.ACTIVE) {
                        _selectedStatusFilter.value.apiValue
                    } else {
                        null
                    }
                    repository.getTalentProposals(status = statusFilter, archived = archived)
                }
                _proposals.value = proposalsList
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement des propositions"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Charger les missions du recruteur pour le filtre
     */
    fun loadMissions() {
        if (!isRecruiter) return
        
        _isLoadingMissions.value = true
        viewModelScope.launch {
            try {
                val missionsList = repository.getRecruiterMissions()
                _missions.value = missionsList
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement des missions"
            } finally {
                _isLoadingMissions.value = false
            }
        }
    }
    
    /**
     * Sélectionner une mission (recruiter)
     */
    fun selectMission(mission: Mission?) {
        _selectedMission.value = mission
        loadProposals()
    }
    
    /**
     * Activer/désactiver le tri AI (recruiter)
     */
    fun toggleAiSort() {
        _aiSortEnabled.value = !_aiSortEnabled.value
        loadProposals()
    }
    
    fun archiveProposal(id: String) {
        viewModelScope.launch {
            try {
                repository.archiveProposal(id)
                loadProposals()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de l'archivage de la proposition"
            }
        }
    }
    
    fun deleteProposal(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteProposal(id)
                loadProposals()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de la suppression de la proposition"
            }
        }
    }
    
    /**
     * Filtrer les propositions avec IA et critères avancés
     */
    fun filterProposals(
        missionId: String? = null,
        minScore: Int? = null,
        maxScore: Int? = null,
        status: String? = null,
        skills: List<String>? = null,
        talentLocation: String? = null,
        minBudget: Int? = null,
        maxBudget: Int? = null,
        sortBy: String? = "score", // "score", "date", "budget"
        sortOrder: String? = "desc" // "asc", "desc"
    ) {
        if (!isRecruiter) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val request = com.example.matchify.data.remote.dto.proposal.ProposalFilterRequestDto(
                    missionId = missionId ?: _selectedMission.value?.missionId,
                    minScore = minScore ?: _filterMinScore.value,
                    maxScore = maxScore,
                    status = status ?: _selectedStatusFilter.value.apiValue,
                    skills = skills ?: _filterSkills.value.takeIf { it.isNotEmpty() },
                    talentLocation = talentLocation,
                    minBudget = minBudget,
                    maxBudget = maxBudget,
                    sortBy = sortBy,
                    sortOrder = sortOrder
                )
                
                val (proposals, response) = repository.filterProposals(request)
                _proposals.value = proposals
                _averageScore.value = response.averageScore
                _filterMinScore.value = minScore
                _filterSkills.value = skills ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du filtrage des propositions"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Recalculer les scores IA pour les propositions de la mission sélectionnée
     */
    fun recalculateProposalScores() {
        if (!isRecruiter) return
        
        val missionId = _selectedMission.value?.missionId ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val proposals = repository.recalculateProposalScores(missionId)
                _proposals.value = proposals
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du recalcul des scores"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Réinitialiser les filtres
     */
    fun resetFilters() {
        _filterMinScore.value = null
        _filterSkills.value = emptyList()
        _averageScore.value = null
        loadProposals()
    }
    
    /**
     * Analyser les propositions avec IA et trouver les meilleures (top 1-2)
     * Utilise l'endpoint existant avec tri AI et filtre côté frontend
     */
    fun analyzeAndFindTopProposals(topCount: Int = 2) {
        if (!isRecruiter) return
        
        val missionId = _selectedMission.value?.missionId ?: return
        
        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null
            
            try {
                // Utiliser l'endpoint existant avec tri AI activé
                val (_, allProposals) = repository.getProposalsForMission(
                    missionId = missionId,
                    aiSort = true // Activer le tri AI
                )
                
                // Filtrer pour prendre les top N propositions avec score IA
                // Les propositions sont déjà triées par score décroissant si aiSort = true
                val topProposalsList = if (allProposals.isNotEmpty()) {
                    // Prendre les propositions avec les meilleurs scores IA
                    allProposals
                        .filter { it.aiScore != null } // Seulement celles avec score IA
                        .sortedByDescending { it.aiScore } // Trier par score décroissant
                        .take(topCount) // Prendre les top N
                } else {
                    // Si pas de scores IA, prendre les premières
                    allProposals.take(topCount)
                }
                
                _topProposals.value = topProposalsList
                
                // Calculer le score moyen des top propositions
                if (topProposalsList.isNotEmpty()) {
                    val scores = topProposalsList.mapNotNull { it.aiScore }
                    _averageScore.value = if (scores.isNotEmpty()) {
                        scores.average()
                    } else {
                        null
                    }
                } else {
                    _averageScore.value = null
                }
                
            } catch (e: Exception) {
                android.util.Log.e("ProposalsViewModel", "Error analyzing top proposals: ${e.message}", e)
                _errorMessage.value = e.message ?: "Erreur lors de l'analyse des propositions"
                _topProposals.value = emptyList()
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    /**
     * Réinitialiser les meilleures propositions
     */
    fun clearTopProposals() {
        _topProposals.value = emptyList()
    }
}

class ProposalsViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProposalsViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val repository = ProposalRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return ProposalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

