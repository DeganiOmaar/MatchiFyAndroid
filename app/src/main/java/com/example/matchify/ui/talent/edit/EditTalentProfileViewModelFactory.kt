package com.example.matchify.ui.talent.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.local.AuthPreferences
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.data.remote.TalentRepository

class EditTalentProfileViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = AuthPreferencesProvider.getInstance().get()
        val apiService = ApiService.getInstance()
        val repository = TalentRepository(apiService.talentApi, prefs)
        val skillRepository = SkillRepository(apiService.skillApi)
        return EditTalentProfileViewModel(repository, skillRepository, context, prefs) as T
    }
}

