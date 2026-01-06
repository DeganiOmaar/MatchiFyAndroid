package com.example.matchify.ui.ratings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.domain.model.Rating
import com.example.matchify.domain.model.TalentRatingsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RatingViewModel : ViewModel() {

    private val _myRating = MutableStateFlow<Rating?>(null)
    val myRating: StateFlow<Rating?> = _myRating.asStateFlow()

    private val _talentRatings = MutableStateFlow<TalentRatingsResult?>(null)
    val talentRatings: StateFlow<TalentRatingsResult?> = _talentRatings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    fun loadMyRating(talentId: String, missionId: String?) {
        // Stub implementation
    }

    fun loadTalentRatings(talentId: String) {
        // Stub implementation
        viewModelScope.launch {
            _talentRatings.value = TalentRatingsResult(
                rating = 0.0,
                averageScore = 0.0,
                count = 0,
                ratings = emptyList()
            )
        }
    }

    fun createOrUpdateRating(
        talentId: String,
        missionId: String?,
        score: Int,
        recommended: Boolean,
        comment: String?,
        tags: List<String>?
    ) {
        // Stub implementation
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(500)
            _saveSuccess.value = true
            _isLoading.value = false
        }
    }
}

class RatingViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RatingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RatingViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
