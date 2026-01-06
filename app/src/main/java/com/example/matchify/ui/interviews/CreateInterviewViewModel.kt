package com.example.matchify.ui.interviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateInterviewViewModel(
    private val interviewRepository: InterviewRepository,
    val proposalId: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()
    
    // Form State
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()
    
    private val _selectedTime = MutableStateFlow<LocalTime?>(null)
    val selectedTime: StateFlow<LocalTime?> = _selectedTime.asStateFlow()
    
    private val _duration = MutableStateFlow(30) // Default 30 min
    val duration: StateFlow<Int> = _duration.asStateFlow()
    
    private val _type = MutableStateFlow("VIDEO") // Default VIDEO
    val type: StateFlow<String> = _type.asStateFlow()
    
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    fun updateDate(date: LocalDate) { _selectedDate.value = date }
    fun updateTime(time: LocalTime) { _selectedTime.value = time }
    fun updateDuration(duration: Int) { _duration.value = duration }
    fun updateType(type: String) { _type.value = type }
    fun updateNotes(notes: String) { _notes.value = notes }

    fun createInterview() {
        if (_selectedDate.value == null || _selectedTime.value == null) {
            _errorMessage.value = "Date and time are required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                interviewRepository.createInterview(
                    proposalId = proposalId,
                    date = _selectedDate.value!!.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time = _selectedTime.value!!.format(DateTimeFormatter.ofPattern("HH:mm")),
                    duration = _duration.value,
                    type = _type.value,
                    notes = _notes.value
                )
                _success.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create interview: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class CreateInterviewViewModelFactory(
    private val proposalId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateInterviewViewModel::class.java)) {
            val api = ApiService.getInstance().interviewApi
            val prefs = AuthPreferencesProvider.getInstance().get()
            val repo = InterviewRepository(api, prefs)
            @Suppress("UNCHECKED_CAST")
            return CreateInterviewViewModel(repo, proposalId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
