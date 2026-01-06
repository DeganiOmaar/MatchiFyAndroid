package com.example.matchify.ui.talent.scored

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.AiRepository

class TalentScoredViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentScoredViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val aiRepository = AiRepository(apiService.aiApi)
            return TalentScoredViewModel(aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

