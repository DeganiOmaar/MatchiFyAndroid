package com.example.matchify.ui.proposals.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ProposalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateProposalViewModel(
    private val missionId: String,
    private val missionTitle: String,
    private val repository: ProposalRepository
) : ViewModel() {
    
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()
    
    private val _proposalContent = MutableStateFlow("")
    val proposalContent: StateFlow<String> = _proposalContent.asStateFlow()
    
    private val _proposedBudget = MutableStateFlow("")
    val proposedBudget: StateFlow<String> = _proposedBudget.asStateFlow()
    
    private val _estimatedDuration = MutableStateFlow("")
    val estimatedDuration: StateFlow<String> = _estimatedDuration.asStateFlow()
    
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    
    private val _isGeneratingAI = MutableStateFlow(false)
    val isGeneratingAI: StateFlow<Boolean> = _isGeneratingAI.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _submissionSuccess = MutableStateFlow(false)
    val submissionSuccess: StateFlow<Boolean> = _submissionSuccess.asStateFlow()
    
    val isFormValid: Boolean
        get() = proposalContent.value.trim().isNotEmpty()
    
    fun updateMessage(text: String) {
        _message.value = text
    }
    
    fun updateProposalContent(text: String) {
        _proposalContent.value = text
    }
    
    fun updateProposedBudget(text: String) {
        _proposedBudget.value = text.filter { it.isDigit() }
    }
    
    fun updateEstimatedDuration(text: String) {
        _estimatedDuration.value = text
    }
    
    fun generateProposalWithAI() {
        _isGeneratingAI.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val generatedContent = repository.generateProposalWithAI(missionId)
                _proposalContent.value = generatedContent
                _isGeneratingAI.value = false
            } catch (e: Exception) {
                _isGeneratingAI.value = false
                _errorMessage.value = e.message ?: "Erreur lors de la génération avec AI"
            }
        }
    }
    
    fun sendProposal() {
        if (!isFormValid) {
            _errorMessage.value = "Please enter a proposal content."
            return
        }
        
        _isSubmitting.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val budgetValue = proposedBudget.value.toIntOrNull()
                val durationValue = estimatedDuration.value.takeIf { it.isNotEmpty() }
                
                repository.createProposal(
                    missionId = missionId,
                    message = message.value.trim().takeIf { it.isNotEmpty() },
                    proposalContent = proposalContent.value.trim(),
                    proposedBudget = budgetValue,
                    estimatedDuration = durationValue
                )
                
                _isSubmitting.value = false
                _submissionSuccess.value = true
            } catch (e: Exception) {
                _isSubmitting.value = false
                _errorMessage.value = e.message ?: "Erreur lors de l'envoi de la proposition"
            }
        }
    }
}

class CreateProposalViewModelFactory(
    private val missionId: String,
    private val missionTitle: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateProposalViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val repository = ProposalRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return CreateProposalViewModel(missionId, missionTitle, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

