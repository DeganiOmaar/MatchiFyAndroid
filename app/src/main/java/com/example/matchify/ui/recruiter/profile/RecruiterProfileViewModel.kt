package com.example.matchify.ui.recruiter.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RecruiterRepository
import com.example.matchify.data.remote.dto.profile.toDomain
import com.example.matchify.data.realtime.ProfileRealtimeClient
import com.example.matchify.data.realtime.ProfileRealtimeEvent
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log // Import Log for better debugging

class RecruiterProfileViewModel(
    private val prefs: AuthPreferences,
    private val repository: RecruiterRepository,
    private val realtimeClient: ProfileRealtimeClient
) : ViewModel() {

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _joinedDate = MutableStateFlow("-")
    val joinedDate: StateFlow<String> = _joinedDate

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        // 🔥 Load user data from local preferences first (like iOS does)
        loadUserFromPreferences()
        // Then fetch fresh data from API
        loadProfile()
        // Observe realtime updates
        observeRealtimeUpdates()
    }
    
    private fun observeRealtimeUpdates() {
        realtimeClient.connect()
        viewModelScope.launch {
            realtimeClient.events.collect { event ->
                when (event) {
                    is ProfileRealtimeEvent.ProfileUpdated -> {
                        // Only update if it's the current user's profile
                        val currentUserId = _user.value?.id
                        if (event.user.id == currentUserId) {
                            _user.value = event.user
                            prefs.saveUser(event.user)
                            updateJoinedDate(event.user.createdAt)
                            Log.d("RecruiterProfileViewModel", "Profile updated via realtime: ${event.user.fullName}")
                        }
                    }
                    is ProfileRealtimeEvent.ProfileDeleted -> {
                        if (event.userId == _user.value?.id) {
                            _errorMessage.value = "Votre profil a été supprimé"
                        }
                    }
                }
            }
        }
    }

    private fun loadUserFromPreferences() {
        viewModelScope.launch {
            try {
                // Load user from local storage first
                val localUser = prefs.user.first()
                if (localUser != null) {
                    _user.value = localUser
                    updateJoinedDate(localUser.createdAt)
                    Log.d("RecruiterProfileViewModel", "Loaded user from preferences: ${localUser.fullName}")
                }
            } catch (e: Exception) {
                Log.e("RecruiterProfileViewModel", "Error loading user from preferences: ${e.message}", e)
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                // Get token directly from DataStore to check if it exists
                val tokenFromStore = prefs.getTokenValue()
                if (tokenFromStore.isNullOrBlank()) {
                    Log.w("RecruiterProfileViewModel", "Token not available in DataStore, skipping API call")
                    // If no token, keep using local data
                    return@launch
                }

                // Small delay to ensure token is synchronized in StateFlow for API interceptor
                kotlinx.coroutines.delay(100)

                val response = repository.getRecruiterProfile()
                Log.d("RecruiterProfileViewModel", "API Response: $response") // Log API response

                val userDto = response.user
                    ?: throw IllegalStateException("Profil recruteur manquant dans la réponse de l'API")
                val userData = userDto.toDomain()
                _user.value = userData
                prefs.saveUser(userData)

                updateJoinedDate(userData.createdAt)
                _errorMessage.value = null // Clear any previous error
            } catch (e: Exception) {
                Log.e("RecruiterProfileViewModel", "Error loading profile: ${e.message}", e) // Log the error
                // Don't show error if we have local data - just log it
                if (_user.value == null) {
                    _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.PROFILE_UPDATE)
                }
            }
        }
    }

    private fun updateJoinedDate(dateString: String?) {
        if (dateString == null) {
            _joinedDate.value = "-"
            return
        }

        try {
            // Parse ISO 8601 date format (e.g., "2024-01-15T10:30:00.000Z")
            val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
            val date = isoFormat.parse(dateString)
            
            if (date != null) {
                // Format as "dd MMMM, yyyy" (e.g., "15 January, 2024")
                val outputFormat = java.text.SimpleDateFormat("dd MMMM, yyyy", java.util.Locale.US)
                _joinedDate.value = outputFormat.format(date)
            } else {
                // Fallback to simple format
                val formatted = dateString.substring(0, 10)  // YYYY-MM-DD
                _joinedDate.value = formatted
            }
        } catch (e: Exception) {
            Log.e("RecruiterProfileViewModel", "Error formatting date: ${e.message}", e)
            // Fallback to simple format
            val formatted = if (dateString.length >= 10) dateString.substring(0, 10) else dateString
            _joinedDate.value = formatted
        }
    }
    
    // Public method to refresh profile after edit
    fun refreshProfile() {
        loadProfile()
    }
}