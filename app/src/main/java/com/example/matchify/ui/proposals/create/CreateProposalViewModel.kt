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
    
    // Validation: proposalContent must be at least 200 characters (same as iOS)
    val isFormValid: Boolean
        get() {
            val trimmed = proposalContent.value.trim()
            return trimmed.length >= 200
        }
    
    fun updateMessage(text: String) {
        _message.value = text
        _errorMessage.value = null
    }
    
    fun updateProposalContent(text: String) {
        _proposalContent.value = text
        _errorMessage.value = null
    }
    
    fun updateProposedBudget(text: String) {
        // Filter out non-numeric characters (same as iOS)
        _proposedBudget.value = text.filter { it.isDigit() }
    }
    
    fun updateEstimatedDuration(text: String) {
        _estimatedDuration.value = text
    }
    
    private var generationJob: kotlinx.coroutines.Job? = null
    
    fun generateWithAI() {
        android.util.Log.d(TAG, "🚀 [VM] generateWithAI called")
        
        if (_isGeneratingAI.value) {
            android.util.Log.w(TAG, "⚠️ [VM] Already generating, ignoring")
            return
        }
        
        android.util.Log.d(TAG, "🔵 [VM] Starting generation, missionId: $missionId")
        _isGeneratingAI.value = true
        _errorMessage.value = null
        _proposalContent.value = "" // Clear existing content
        
        generationJob = viewModelScope.launch {
            android.util.Log.d(TAG, "🔵 [VM] Job started, creating stream...")
            var chunkCount = 0
            
            try {
                repository.generateProposalWithAIStream(missionId).collect { chunk ->
                    chunkCount++
                    android.util.Log.d(TAG, "📝 [VM] Chunk #$chunkCount received: ${chunk.take(50)}...")
                    
                    _proposalContent.value += chunk
                }
                
                android.util.Log.d(TAG, "✅ [VM] Stream ended, total chunks: $chunkCount")
                
                _isGeneratingAI.value = false
                
                if (chunkCount == 0) {
                    _errorMessage.value = "Aucun contenu généré. Veuillez réessayer."
                }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "❌ [VM] Stream error: ${e.message}", e)
                _isGeneratingAI.value = false
                _errorMessage.value = "La génération IA n'est pas disponible. Veuillez écrire votre proposition manuellement."
            }
        }
    }
    
    fun cancelGeneration() {
        android.util.Log.d(TAG, "🛑 [VM] Cancelling generation")
        generationJob?.cancel()
        generationJob = null
        _isGeneratingAI.value = false
    }
    
    fun sendProposal() {
        if (_isSubmitting.value) return
        
        // Validation: same as iOS - must be at least 200 characters
        if (!isFormValid) {
            _errorMessage.value = "La proposition doit contenir au moins 200 caractères."
            return
        }
        
        _isSubmitting.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val budgetValue = _proposedBudget.value.toIntOrNull()
                val durationValue = _estimatedDuration.value.takeIf { it.isNotEmpty() }
                
                repository.createProposal(
                    missionId = missionId,
                    message = _message.value.trim().takeIf { it.isNotEmpty() },
                    proposalContent = _proposalContent.value.trim(),
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
    
    companion object {
        private const val TAG = "CreateProposalVM"
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

