package com.example.matchify.ui.proposals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ProposalRepository
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
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    init {
        loadProposals()
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
                    repository.getRecruiterProposals()
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

