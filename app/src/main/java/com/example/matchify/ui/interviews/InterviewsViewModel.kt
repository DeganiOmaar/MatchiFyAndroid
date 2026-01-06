package com.example.matchify.ui.interviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.domain.model.Interview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InterviewsViewModel(
    private val repository: InterviewRepository
) : ViewModel() {
    
    private val _interviews = MutableStateFlow<List<Interview>>(emptyList())
    val interviews: StateFlow<List<Interview>> = _interviews.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadInterviews()
    }
    

    
    val isRecruiter: Boolean
        get() = repository.isRecruiter

    fun loadInterviews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val interviews = repository.getMyInterviews()
                _interviews.value = interviews
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Une erreur est survenue"
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
            try {
                // Note: cancellationReason is effectively unused if backend doesn't support it yet
                repository.cancelInterview(interviewId)
                loadInterviews() // Reload list
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to cancel interview")
                _errorMessage.value = e.message
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
        if (modelClass.isAssignableFrom(InterviewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InterviewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
