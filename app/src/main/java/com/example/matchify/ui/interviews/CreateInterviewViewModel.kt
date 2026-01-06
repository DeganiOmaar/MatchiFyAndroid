package com.example.matchify.ui.interviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.InterviewRepository
import com.example.matchify.data.remote.ProposalRepository
import com.example.matchify.domain.model.ProposalStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

class CreateInterviewViewModel(
    private val proposalId: String
) : ViewModel() {
    
    private val repository: InterviewRepository by lazy {
        val authPreferences = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        InterviewRepository(apiService.interviewApi, authPreferences)
    }
    
    private val proposalRepository: ProposalRepository by lazy {
        val authPreferences = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        ProposalRepository(apiService, authPreferences)
    }
    
    val scheduledDate = MutableStateFlow<Date?>(null)
    val notes = MutableStateFlow("")
    val meetLink = MutableStateFlow("")
    val useAutoGenerate = MutableStateFlow(true) // Par défaut, génération automatique
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val success = MutableStateFlow(false)
    
    fun setScheduledDate(date: Date) {
        scheduledDate.value = date
    }
    
    fun setNotes(notesText: String) {
        notes.value = notesText
    }
    
    fun setMeetLink(link: String) {
        meetLink.value = link
    }
    
    fun setUseAutoGenerate(useAuto: Boolean) {
        useAutoGenerate.value = useAuto
    }
    
    fun createInterview() {
        val date = scheduledDate.value
        
        if (date == null) {
            _errorMessage.value = "Veuillez sélectionner une date et une heure"
            return
        }
        
        // Si mode manuel, vérifier que le lien est fourni
        if (!useAutoGenerate.value && meetLink.value.isBlank()) {
            _errorMessage.value = "Veuillez saisir un lien de réunion ou activer la génération automatique"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.createInterview(
                    proposalId = proposalId,
                    scheduledAt = date,
                    meetLink = if (useAutoGenerate.value) null else meetLink.value.takeIf { it.isNotBlank() },
                    notes = notes.value.takeIf { it.isNotBlank() },
                    autoGenerateMeetLink = useAutoGenerate.value
                )
                
                // Mettre à jour le statut de la proposal en ACCEPTED après création de l'interview
                try {
                    proposalRepository.updateProposalStatus(proposalId, ProposalStatus.ACCEPTED.name)
                } catch (e: Exception) {
                    // Log l'erreur mais ne bloque pas le succès de la création d'interview
                    android.util.Log.e("CreateInterviewViewModel", "Erreur lors de la mise à jour du statut de la proposal", e)
                }
                
                success.value = true
            } catch (e: Exception) {
                // Détecter spécifiquement l'erreur HTTP 503
                val is503Error = e is HttpException && e.code() == 503
                
                if (is503Error) {
                    _errorMessage.value = "Impossible de générer automatiquement le lien de réunion (Zoom/Meet indisponible). Veuillez réessayer plus tard."
                } else {
                    val errorMsg = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                    // Vérifier aussi dans le message d'erreur pour les cas où le code n'est pas 503 mais le message l'indique
                    if (errorMsg.contains("503") || errorMsg.contains("indisponible") || 
                        errorMsg.contains("Zoom") || errorMsg.contains("Google Calendar") || errorMsg.contains("générer")) {
                        _errorMessage.value = "Impossible de générer automatiquement le lien de réunion. Veuillez réessayer plus tard."
                    } else {
                        _errorMessage.value = errorMsg
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CreateInterviewViewModelFactory(
    private val proposalId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CreateInterviewViewModel(proposalId) as T
    }
}

