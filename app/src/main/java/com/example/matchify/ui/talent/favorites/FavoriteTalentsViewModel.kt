package com.example.matchify.ui.talent.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.TalentFavoriteRepository
import com.example.matchify.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteTalentsViewModel(
    private val repository: TalentFavoriteRepository
) : ViewModel() {

    private val _favoriteTalents = MutableStateFlow<List<UserModel>>(emptyList())
    val favoriteTalents: StateFlow<List<UserModel>> = _favoriteTalents

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadFavoriteTalents() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val talents = repository.getFavoriteTalents()
                _favoriteTalents.value = talents
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement des talents favoris"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFavoriteTalent(talentId: String) {
        viewModelScope.launch {
            try {
                repository.removeFavoriteTalent(talentId)
                // Recharger la liste après suppression
                loadFavoriteTalents()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de la suppression du talent favori"
            }
        }
    }
}

