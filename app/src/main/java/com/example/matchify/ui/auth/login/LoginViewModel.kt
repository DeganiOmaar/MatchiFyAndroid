package com.example.matchify.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository,
    private val prefs: AuthPreferences
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _showPassword = MutableStateFlow(false)
    val showPassword: StateFlow<Boolean> = _showPassword

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    // ⭐ NEW: Navigation destination after successful login
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo


    fun setEmail(value: String) {
        _email.value = value
    }

    fun setPassword(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _showPassword.value = !_showPassword.value
    }

    fun login(rememberMe: Boolean) {
        _error.value = null
        
        // Validation
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Veuillez remplir tous les champs."
            return
        }
        
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.login(
                    email = _email.value,
                    password = _password.value
                )

                // SAVE SESSION
                prefs.saveToken(response.token)
                prefs.saveUser(response.user)
                prefs.saveRememberMe(rememberMe)

                _isLoading.value = false
                _loginSuccess.value = true

                // ⭐ ROLE-BASED NAVIGATION
                // Both Talent and Recruiter go to "main" (Missions + Profile with bottom nav)
                _navigateTo.value = "main"

            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = ErrorHandler.getErrorMessage(e, ErrorContext.LOGIN)
            }
        }
    }

    fun onNavigationDone() {
        _navigateTo.value = null
    }
}