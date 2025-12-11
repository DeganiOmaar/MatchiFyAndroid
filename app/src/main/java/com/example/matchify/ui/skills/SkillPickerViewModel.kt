package com.example.matchify.ui.skills

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.SkillRepository
import com.example.matchify.domain.model.Skill
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SkillPickerViewModel(
    private val repository: SkillRepository
) : ViewModel() {
    
    private val _suggestions = MutableStateFlow<List<Skill>>(emptyList())
    val suggestions: StateFlow<List<Skill>> = _suggestions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var searchJob: Job? = null
    
    fun searchSkills(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            
            if (query.isBlank()) {
                _suggestions.value = emptyList()
                _isLoading.value = false
                return@launch
            }
            
            _isLoading.value = true
            try {
                val results = repository.searchSkills(query)
                _suggestions.value = results
            } catch (e: Exception) {
                _suggestions.value = emptyList()
                android.util.Log.e("SkillPickerViewModel", "Error searching skills: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearSuggestions() {
        searchJob?.cancel()
        _suggestions.value = emptyList()
        _isLoading.value = false
    }
}

class SkillPickerViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkillPickerViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val repository = SkillRepository(apiService.skillApi)
            @Suppress("UNCHECKED_CAST")
            return SkillPickerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

