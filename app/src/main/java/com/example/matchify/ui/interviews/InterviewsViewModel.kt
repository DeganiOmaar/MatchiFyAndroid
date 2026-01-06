package com.example.matchify.ui.interviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.InterviewRepository
import com.example.matchify.domain.model.Interview
import com.example.matchify.domain.model.InterviewStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class InterviewsViewModel(
    private val repository: InterviewRepository
) : ViewModel() {
    
    private val _interviews = MutableStateFlow<List<Interview>>(emptyList())
    val interviews: StateFlow<List<Interview>> = _interviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    init {
        loadInterviews()
    }
    
    fun loadInterviews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val interviewsList = if (isRecruiter) {
                    repository.getRecruiterInterviews()
                } else {
                    repository.getTalentInterviews()
                }
                // Filtrer les interviews annulées de la liste
                _interviews.value = interviewsList
                    .filter { it.status != InterviewStatus.CANCELLED }
                    .sortedBy { it.scheduledDate }
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createInterview(
        proposalId: String,
        scheduledAt: Date,
        meetLink: String,
        notes: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.createInterview(proposalId, scheduledAt, meetLink, notes)
                
                // Recharger la liste
                loadInterviews()
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateInterview(
        interviewId: String,
        scheduledAt: Date? = null,
        meetLink: String? = null,
        status: String? = null,
        notes: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val scheduledAtStr = scheduledAt?.let {
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    formatter.timeZone = TimeZone.getTimeZone("UTC")
                    formatter.format(it)
                }
                
                repository.updateInterview(interviewId, scheduledAtStr, meetLink, status, notes)
                
                // Recharger la liste
                loadInterviews()
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun cancelInterview(
        interviewId: String,
        cancellationReason: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                repository.cancelInterview(interviewId, cancellationReason)
                
                // Filtrer l'interview annulée de la liste (supprimer de la liste)
                _interviews.value = _interviews.value.filter { it.interviewId != interviewId }
                
                onSuccess()
            } catch (e: Exception) {
                val errorMsg = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                _errorMessage.value = errorMsg
                onError(errorMsg)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class InterviewsViewModelFactory(
    private val repository: InterviewRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return InterviewsViewModel(repository) as T
    }
}

