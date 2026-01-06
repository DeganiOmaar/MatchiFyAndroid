package com.example.matchify.ui.talent.profilebyid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.UserRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.TalentFavoriteRepository
import com.example.matchify.domain.model.Project
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TalentProfileByIDViewModel(
    private val repository: UserRepository,
    private val skillRepository: com.example.matchify.data.remote.SkillRepository,
    private val favoriteRepository: TalentFavoriteRepository,
    private val talentId: String
) : ViewModel() {
    
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user.asStateFlow()
    
    private val _portfolio = MutableStateFlow<List<Project>>(emptyList())
    val portfolio: StateFlow<List<Project>> = _portfolio.asStateFlow()
    
    private val _skillNames = MutableStateFlow<List<String>>(emptyList())
    val skillNames: StateFlow<List<String>> = _skillNames.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    private val _isLoadingFavorite = MutableStateFlow(false)
    val isLoadingFavorite: StateFlow<Boolean> = _isLoadingFavorite.asStateFlow()
    
    fun loadProfile() {
        if (_isLoading.value) return
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = repository.getUserById(talentId)
                _user.value = result.first
                _portfolio.value = result.second
                loadSkillNames()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load profile"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun loadSkillNames() {
        val skillIds = _user.value?.skills
        if (skillIds.isNullOrEmpty()) {
            _skillNames.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            try {
                val skills = skillRepository.getSkillsByIds(skillIds)
                _skillNames.value = skills.map { it.name }
            } catch (e: Exception) {
                // Fallback: use IDs if loading names fails
                _skillNames.value = skillIds
            }
        }
    }
    
    fun checkIfFavorite() {
        viewModelScope.launch {
            try {
                android.util.Log.d("TalentProfileByIDViewModel", "Checking if talent $talentId is favorite")
                val favoriteTalents = favoriteRepository.getFavoriteTalents()
                android.util.Log.d("TalentProfileByIDViewModel", "Found ${favoriteTalents.size} favorite talents")
                val isFav = favoriteTalents.any { it.id == talentId }
                android.util.Log.d("TalentProfileByIDViewModel", "Talent $talentId is favorite: $isFav")
                _isFavorite.value = isFav
            } catch (e: Exception) {
                android.util.Log.e("TalentProfileByIDViewModel", "Error checking favorite status: ${e.message}", e)
                _errorMessage.value = "Erreur lors de la vérification des favoris: ${e.message}"
            }
        }
    }
    
    fun toggleFavorite() {
        if (_isLoadingFavorite.value) return
        
        val wasFavorite = _isFavorite.value
        val newFavoriteState = !wasFavorite
        android.util.Log.d("TalentProfileByIDViewModel", "Toggling favorite for talent $talentId: $wasFavorite -> $newFavoriteState")
        
        // Optimistic update
        _isFavorite.value = newFavoriteState
        _isLoadingFavorite.value = true
        
        viewModelScope.launch {
            try {
                if (wasFavorite) {
                    android.util.Log.d("TalentProfileByIDViewModel", "Removing talent $talentId from favorites")
                    favoriteRepository.removeFavoriteTalent(talentId)
                    android.util.Log.d("TalentProfileByIDViewModel", "Successfully removed talent from favorites")
                } else {
                    android.util.Log.d("TalentProfileByIDViewModel", "Adding talent $talentId to favorites")
                    favoriteRepository.addFavoriteTalent(talentId)
                    android.util.Log.d("TalentProfileByIDViewModel", "Successfully added talent to favorites")
                }
                // Recheck favorite status to ensure sync with backend
                checkIfFavorite()
            } catch (e: Exception) {
                android.util.Log.e("TalentProfileByIDViewModel", "Error toggling favorite: ${e.message}", e)
                // Revert on error
                _isFavorite.value = wasFavorite
                _errorMessage.value = e.message ?: "Erreur lors de la mise à jour des favoris"
            } finally {
                _isLoadingFavorite.value = false
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
            val skillRepository = com.example.matchify.data.remote.SkillRepository(apiService.skillApi)
            val favoriteRepository = TalentFavoriteRepository(apiService, authPreferences)
            @Suppress("UNCHECKED_CAST")
            return TalentProfileByIDViewModel(repository, skillRepository, favoriteRepository, talentId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

