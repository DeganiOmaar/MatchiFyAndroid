package com.example.matchify.ui.recruiter.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.RecruiterRepository

class EditRecruiterProfileViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditRecruiterProfileViewModel::class.java)) {
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val recruiterApi = ApiService.getInstance().recruiterApi
            val recruiterRepository = RecruiterRepository(recruiterApi, authPreferences)

            return EditRecruiterProfileViewModel(recruiterRepository, context, authPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}