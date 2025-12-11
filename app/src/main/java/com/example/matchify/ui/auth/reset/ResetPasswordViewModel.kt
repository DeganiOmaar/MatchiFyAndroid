package com.example.matchify.ui.auth.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _newPassword = MutableStateFlow("")
    val newPassword: StateFlow<String> = _newPassword

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _showNewPassword = MutableStateFlow(false)
    val showNewPassword: StateFlow<Boolean> = _showNewPassword

    private val _showConfirmPassword = MutableStateFlow(false)
    val showConfirmPassword: StateFlow<Boolean> = _showConfirmPassword

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun setNewPassword(value: String) {
        _newPassword.value = value
    }

    fun setConfirmPassword(value: String) {
        _confirmPassword.value = value
    }

    fun toggleNewPassword() {
        _showNewPassword.value = !_showNewPassword.value
    }

    fun toggleConfirmPassword() {
        _showConfirmPassword.value = !_showConfirmPassword.value
    }

    fun reset() {
        _error.value = null
        
        // Validation
        if (_newPassword.value.isBlank() || _confirmPassword.value.isBlank()) {
            _error.value = "Veuillez remplir tous les champs."
            return
        }
        
        if (_newPassword.value != _confirmPassword.value) {
            _error.value = "Les mots de passe ne correspondent pas."
            return
        }
        
        if (_newPassword.value.length < 6) {
            _error.value = "Le mot de passe doit contenir au moins 6 caractères."
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.resetPassword(
                    newPassword = _newPassword.value,
                    confirmPassword = _confirmPassword.value
                )

                _isLoading.value = false
                _success.value = true

            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = ErrorHandler.getErrorMessage(e, ErrorContext.PASSWORD_RESET)
            }
        }
    }
}

class ResetPasswordViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            // Get singleton instances of dependencies
            val authApi = ApiService.getInstance().authApi
            val authRepository = AuthRepository(authApi)

            return ResetPasswordViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}