package com.example.matchify.ui.portfolio.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import com.example.matchify.domain.model.Project
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.data.remote.SkillRepository

class AddEditProjectViewModelFactory(
    private val project: Project? = null,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditProjectViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val portfolioRepository = PortfolioRepository(apiService.portfolioApi, authPreferences, context.contentResolver)
            val skillRepository = SkillRepository(apiService.skillApi)
            @Suppress("UNCHECKED_CAST")
            return AddEditProjectViewModel(portfolioRepository, skillRepository, context.contentResolver, project) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
