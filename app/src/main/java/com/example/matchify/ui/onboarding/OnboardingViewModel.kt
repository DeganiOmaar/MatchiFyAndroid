package com.example.matchify.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val prefs: AuthPreferences
) : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    val isFirstPage: Boolean
        get() = _currentPage.value == 0

    val isLastPage: Boolean
        get() = _currentPage.value == 2

    fun nextPage() {
        if (_currentPage.value < 2) {
            _currentPage.value = _currentPage.value + 1
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value = _currentPage.value - 1
        }
    }
    
    fun setCurrentPage(page: Int) {
        if (page >= 0 && page <= 2) {
            _currentPage.value = page
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            prefs.saveHasSeenOnboarding(true)
        }
    }
}

