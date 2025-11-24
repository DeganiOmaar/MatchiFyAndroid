package com.example.matchify.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.domain.session.AuthSessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authPreferences: AuthPreferences,
    private val authSessionManager: AuthSessionManager
) : ViewModel() {

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _logoutEvents = MutableSharedFlow<Unit>(replay = 0)
    val logoutEvents: SharedFlow<Unit> = _logoutEvents

    fun setLanguage(code: String) {
        viewModelScope.launch {
            authPreferences.saveLanguage(code)
        }
    }

    fun logout() {
        if (_isLoggingOut.value) return

        viewModelScope.launch {
            _isLoggingOut.value = true
            _errorMessage.value = null
            
            runCatching {
                authSessionManager.logout()
            }.onSuccess {
                _logoutEvents.emit(Unit)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.localizedMessage ?: "Une erreur est survenue."
            }
            
            _isLoggingOut.value = false
        }
    }
}

