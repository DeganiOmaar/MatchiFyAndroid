package com.example.matchify.ui.auth.signup.talent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.dto.auth.TalentSignupRequest
import com.example.matchify.data.realtime.RealtimeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TalentSignupViewModel(
    private val repository: AuthRepository,
    private val prefs: AuthPreferences
) : ViewModel() {

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location

    private val _talent = MutableStateFlow("")
    val talent: StateFlow<String> = _talent

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

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _signupSuccess = MutableStateFlow(false)
    val signupSuccess: StateFlow<Boolean> = _signupSuccess

    // Navigation destination based on role
    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> = _navigateTo


    fun setFullName(v: String) { _fullName.value = v }
    fun setEmail(v: String) { _email.value = v }
    fun setPhone(v: String) { _phone.value = v }
    fun setLocation(v: String) { _location.value = v }
    fun setTalent(v: String) { _talent.value = v }
    fun setPassword(v: String) { _password.value = v }
    fun setConfirmPassword(v: String) { _confirmPassword.value = v }

    fun togglePasswordVisibility() {
        _showPassword.value = !_showPassword.value
    }

    fun toggleConfirmPasswordVisibility() {
        _showConfirmPassword.value = !_showConfirmPassword.value
    }

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

        _loading.value = true

        viewModelScope.launch {
            try {
                val body = TalentSignupRequest(
                    fullName = _fullName.value,
                    email = _email.value,
                    password = _password.value,
                    confirmPassword = _confirmPassword.value,
                    phone = _phone.value,
                    profileImage = "",  // same as iOS placeholder
                    location = _location.value,
                    talent = _talent.value
                )

                val response = repository.signupTalent(body)

                // Save session (same as login)
                prefs.saveToken(response.token)
                prefs.saveUser(response.user)
                prefs.saveRememberMe(true)

                // Connect realtime clients after successful signup
                try {
                    val realtimeManager = RealtimeManager.getInstance()
                    realtimeManager.connectAll()
                } catch (e: Exception) {
                    android.util.Log.e("TalentSignupViewModel", "Failed to connect realtime clients", e)
                }

                _loading.value = false
                _signupSuccess.value = true
                
                // ⭐ ROLE-BASED NAVIGATION (same as login)
                // Both Talent and Recruiter go to "main" (Missions + Profile with bottom nav)
                _navigateTo.value = "main"

            } catch (e: Exception) {
                _loading.value = false
                _error.value = ErrorHandler.getErrorMessage(e, ErrorContext.SIGNUP)
            }
        }
    }

    fun onNavigationDone() {
        _navigateTo.value = null
    }
}