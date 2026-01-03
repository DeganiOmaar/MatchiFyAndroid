package com.example.matchify.ui.proposals.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ConversationRepository
import com.example.matchify.data.remote.ProposalRepository
import com.example.matchify.domain.model.Conversation
import com.example.matchify.domain.model.Proposal
import com.example.matchify.domain.model.ProposalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ProposalDetailsViewModel(
    private val proposalId: String,
    private val proposalRepository: ProposalRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    
    private val _proposal = MutableStateFlow<Proposal?>(null)
    val proposal: StateFlow<Proposal?> = _proposal.asStateFlow()
    
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()
    
    val conversationId: String?
        get() = _conversation.value?.conversationId
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isUpdatingStatus = MutableStateFlow(false)
    val isUpdatingStatus: StateFlow<Boolean> = _isUpdatingStatus.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    val isTalent: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "talent"
        }
    
    val canShowActions: StateFlow<Boolean> = MutableStateFlow(false).also { flow ->
        viewModelScope.launch {
            _proposal.collect { proposal ->
                val canShow = isRecruiter && proposal != null &&
                        (proposal.status == ProposalStatus.NOT_VIEWED ||
                         proposal.status == ProposalStatus.VIEWED)
                flow.value = canShow
            }
        }
    }
    
    val showMessageButton: StateFlow<Boolean> = MutableStateFlow(false).also { flow ->
        viewModelScope.launch {
            _proposal.collect { proposal ->
                val show = isRecruiter && proposal != null &&
                        proposal.status == ProposalStatus.ACCEPTED
                flow.value = show
                if (show) {
                    loadConversation()
                }
            }
        }
    }
    
    init {
        loadProposal()
    }
    
    fun loadProposal() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val proposal = proposalRepository.getProposalById(proposalId)
                _proposal.value = proposal
                
                // Auto-mark as viewed when opened (for recruiter)
                if (isRecruiter && proposal.status == ProposalStatus.NOT_VIEWED) {
                    updateStatus(ProposalStatus.VIEWED.name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun acceptProposal() {
        updateStatus(ProposalStatus.ACCEPTED.name)
    }
    
    fun refuseProposal(reason: String) {
        updateStatus(ProposalStatus.REFUSED.name, reason)
    }
    
    private fun updateStatus(status: String, rejectionReason: String? = null) {
        viewModelScope.launch {
            _isUpdatingStatus.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("ProposalDetailsVM", "Updating proposal status to: $status, reason: $rejectionReason")
                val updatedProposal = proposalRepository.updateProposalStatus(proposalId, status, rejectionReason)
                _proposal.value = updatedProposal
                android.util.Log.d("ProposalDetailsVM", "Proposal updated successfully: ${updatedProposal.status}")
                
                // If accepted, create conversation
                if (status == ProposalStatus.ACCEPTED.name) {
                    loadConversation()
                }
            } catch (e: Exception) {
                android.util.Log.e("ProposalDetailsVM", "Error updating proposal status", e)
                _errorMessage.value = "Failed to update proposal: ${e.message}"
                e.printStackTrace()
            } finally {
                _isUpdatingStatus.value = false
            }
        }
    }
    
    fun loadConversation() {
        viewModelScope.launch {
            try {
                val proposal = _proposal.value ?: return@launch
                
                // Try to find existing conversation
                val conversations = conversationRepository.getConversations()
                val existing = conversations.find {
                    it.missionId == proposal.missionId &&
                    ((it.recruiterId == proposal.recruiterId && it.talentId == proposal.talentId) ||
                     (it.talentId == proposal.talentId && it.recruiterId == proposal.recruiterId))
                }
                
                if (existing != null) {
                    _conversation.value = existing
                } else {
                    // Create new conversation
                    val newConversation = conversationRepository.createConversation(
                        missionId = proposal.missionId,
                        talentId = proposal.talentId,
                        recruiterId = proposal.recruiterId
                    )
                    _conversation.value = newConversation
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ProposalDetailsViewModelFactory(
    private val proposalId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProposalDetailsViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val proposalRepo = ProposalRepository(apiService, prefs)
            val conversationRepo = ConversationRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return ProposalDetailsViewModel(proposalId, proposalRepo, conversationRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

