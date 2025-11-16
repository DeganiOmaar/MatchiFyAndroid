package com.example.matchify.ui.auth.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AuthRepository
import com.example.matchify.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VerifyCodeViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun updateCode(value: String) {
        if (value.length <= 6) _code.value = value
    }

    fun verify() {
        if (_code.value.length != 6) return

        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                repository.verifyResetCode(_code.value)
                _loading.value = false
                _success.value = true
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message
            }
        }
    }
}

class VerifyCodeViewModelFactory :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyCodeViewModel::class.java)) {
            val authApi = ApiService.getInstance().authApi
            val authRepository = AuthRepository(authApi)

            return VerifyCodeViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}