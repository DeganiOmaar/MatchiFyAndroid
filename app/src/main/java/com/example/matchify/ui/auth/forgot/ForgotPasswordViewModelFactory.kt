package com.example.matchify.ui.auth.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.AuthRepository

class ForgotPasswordViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            // Get singleton instances of dependencies
            val authApi = ApiService.getInstance().authApi
            val authRepository = AuthRepository(authApi)

            return ForgotPasswordViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
