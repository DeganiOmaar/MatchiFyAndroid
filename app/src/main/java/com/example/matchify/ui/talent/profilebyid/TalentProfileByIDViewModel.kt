package com.example.matchify.ui.talent.profilebyid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.UserRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TalentProfileByIDViewModel(
    private val repository: UserRepository,
    private val talentId: String
) : ViewModel() {
    
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()
    
    private val _portfolio = MutableStateFlow<List<Project>>(emptyList())
    val portfolio: StateFlow<List<Project>> = _portfolio.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun loadProfile() {
        if (_isLoading.value) return
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.getUserById(talentId)
                _user.value = result.first
                _portfolio.value = result.second
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class TalentProfileByIDViewModelFactory(
    private val talentId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TalentProfileByIDViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val authPreferences = AuthPreferencesProvider.getInstance().get()
            val repository = UserRepository(apiService.userApi, authPreferences)
            @Suppress("UNCHECKED_CAST")
            return TalentProfileByIDViewModel(repository, talentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

