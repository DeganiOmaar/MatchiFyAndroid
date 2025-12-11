package com.example.matchify.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _codeSent = MutableStateFlow(false)
    val codeSent: StateFlow<Boolean> = _codeSent

    fun setEmail(value: String) {
        _email.value = value
    }

    fun sendCode() {
        _error.value = null
        
        // Validation
        if (_email.value.isBlank()) {
            _error.value = "Veuillez entrer votre adresse email."
            return
        }
        
        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.forgotPassword(_email.value)
                _isLoading.value = false
                _codeSent.value = true
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = ErrorHandler.getErrorMessage(e, ErrorContext.FORGOT_PASSWORD)
            }
        }
    }
}