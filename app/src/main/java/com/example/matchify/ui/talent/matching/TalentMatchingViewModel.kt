package com.example.matchify.ui.talent.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.TalentRepository
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.domain.model.Talent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TalentMatchingViewModel(
    private val talentRepository: TalentRepository
) : ViewModel() {

    private val _talents = MutableStateFlow<List<Talent>>(emptyList())
    val talents: StateFlow<List<Talent>> = _talents.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadMatchedTalentsForMission(missionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Since we don't have a real matching endpoint yet, we fetch all talents
                // In a real app, this would call a specific matching endpoint
                val allTalents = talentRepository.getAllTalents()
                _talents.value = allTalents
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class TalentMatchingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentMatchingViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val prefs = AuthPreferencesProvider.getInstance().get()
            val repository = TalentRepository(apiService.talentApi, prefs)
            @Suppress("UNCHECKED_CAST")
            return TalentMatchingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
