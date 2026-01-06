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
    
    // Form State matching Screen expectations
    private val _scheduledDate = MutableStateFlow<java.util.Date?>(null)
    val scheduledDate: StateFlow<java.util.Date?> = _scheduledDate.asStateFlow()
    
    private val _useAutoGenerate = MutableStateFlow(false)
    val useAutoGenerate: StateFlow<Boolean> = _useAutoGenerate.asStateFlow()
    
    private val _meetLink = MutableStateFlow("")
    val meetLink: StateFlow<String> = _meetLink.asStateFlow()
    
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    fun setScheduledDate(date: java.util.Date) { _scheduledDate.value = date }
    fun setUseAutoGenerate(enabled: Boolean) { _useAutoGenerate.value = enabled }
    fun setMeetLink(link: String) { _meetLink.value = link }
    fun setNotes(notes: String) { _notes.value = notes }

    fun createInterview() {
        val date = _scheduledDate.value
        if (date == null) {
            _errorMessage.value = "Date is required"
            return
        }
        
        // Validation logic if needed (e.g. check link if manual)

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Format date to ISO string
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val scheduledAt = inputFormat.format(date)

                interviewRepository.createInterview(
                    proposalId = proposalId,
                    scheduledAt = scheduledAt,
                    notes = _notes.value,
                    autoGenerateMeetLink = _useAutoGenerate.value,
                    meetLink = if (_useAutoGenerate.value) null else _meetLink.value
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
