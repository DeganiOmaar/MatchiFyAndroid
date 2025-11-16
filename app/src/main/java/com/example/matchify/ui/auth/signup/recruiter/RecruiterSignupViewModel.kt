package com.example.matchify.ui.auth.signup.recruiter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.RecruiterSignupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecruiterSignupViewModel(
    private val repository: AuthRepository,
    private val prefs: AuthPreferences
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _showPassword = MutableStateFlow(false)
    val showPassword: StateFlow<Boolean> = _showPassword

    private val _showConfirmPassword = MutableStateFlow(false)
    val showConfirmPassword: StateFlow<Boolean> = _showConfirmPassword

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _signupSuccess = MutableStateFlow(false)
    val signupSuccess: StateFlow<Boolean> = _signupSuccess

    fun setFullName(v: String) { _fullName.value = v }
    fun setEmail(v: String) { _email.value = v }
    fun setPassword(v: String) { _password.value = v }
    fun setConfirmPassword(v: String) { _confirmPassword.value = v }

    fun toggleShowPassword() { _showPassword.value = !_showPassword.value }
    fun toggleShowConfirmPassword() { _showConfirmPassword.value = !_showConfirmPassword.value }

    fun signUp() {
        _error.value = null

        // Validation
        if (_fullName.value.isBlank() || _email.value.isBlank() || 
            _password.value.isBlank() || _confirmPassword.value.isBlank()) {
            _error.value = "Veuillez remplir tous les champs requis."
            return
        }

        if (_password.value != _confirmPassword.value) {
            _error.value = "Les mots de passe ne correspondent pas."
            return
        }

        if (_password.value.length < 6) {
            _error.value = "Le mot de passe doit contenir au moins 6 caractères."
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val body = RecruiterSignupRequest(
                    fullName = _fullName.value,
                    email = _email.value,
                    password = _password.value,
                    confirmPassword = _confirmPassword.value
                )

                val response = repository.signupRecruiter(body)

                prefs.saveToken(response.token)
                prefs.saveUser(response.user)
                prefs.saveRememberMe(true)

                _isLoading.value = false
                _signupSuccess.value = true

            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = ErrorHandler.getErrorMessage(e, ErrorContext.SIGNUP)
            }
        }
    }
}