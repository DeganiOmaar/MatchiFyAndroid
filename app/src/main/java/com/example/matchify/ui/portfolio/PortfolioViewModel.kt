package com.example.matchify.ui.portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.domain.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PortfolioViewModel(
    private val repository: PortfolioRepository
) : ViewModel() {
    
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()
    
    private val itemsPerPage = 4
    
    val totalPages: Int
        get() = kotlin.math.max(1, kotlin.math.ceil(_projects.value.size.toDouble() / itemsPerPage.toDouble()).toInt())
    
    val currentPageProjects: List<Project>
        get() {
            val startIndex = (_currentPage.value - 1) * itemsPerPage
            val endIndex = kotlin.math.min(startIndex + itemsPerPage, _projects.value.size)
            if (startIndex >= _projects.value.size) return emptyList()
            return _projects.value.subList(startIndex, endIndex)
        }
    
    val canGoBack: Boolean
        get() = _currentPage.value > 1
    
    val canGoNext: Boolean
        get() = _currentPage.value < totalPages
    
    init {
        loadProjects()
    }
    
    fun loadProjects() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val projectsList = repository.getProjects()
                _projects.value = projectsList
                _currentPage.value = 1 // Reset to first page
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load projects"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun goToNextPage() {
        if (canGoNext) {
            _currentPage.value = _currentPage.value + 1
        }
    }
    
    fun goToPreviousPage() {
        if (canGoBack) {
            _currentPage.value = _currentPage.value - 1
        }
    }
}

class PortfolioViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PortfolioViewModel::class.java)) {
            val apiService = com.example.matchify.data.remote.ApiService.getInstance()
            val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
            val repository = PortfolioRepository(apiService.portfolioApi, authPreferences)
            @Suppress("UNCHECKED_CAST")
            return PortfolioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

