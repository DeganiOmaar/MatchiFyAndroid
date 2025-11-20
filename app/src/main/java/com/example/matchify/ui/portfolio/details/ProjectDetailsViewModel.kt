package com.example.matchify.ui.portfolio.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.PortfolioRepository
import com.example.matchify.domain.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectDetailsViewModel(
    private val repository: PortfolioRepository,
    private val projectId: String
) : ViewModel() {
    
    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> = _project.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()
    
    init {
        loadProject()
    }
    
    fun loadProject() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                android.util.Log.d("ProjectDetailsViewModel", "Loading project with ID: $projectId")
                val projectData = repository.getProjectById(projectId)
                android.util.Log.d("ProjectDetailsViewModel", "Project loaded: ${projectData.title}, description: ${projectData.description?.take(50)}, media count: ${projectData.media.size}, skills: ${projectData.skills.size}")
                _project.value = projectData
            } catch (e: Exception) {
                android.util.Log.e("ProjectDetailsViewModel", "Error loading project: ${e.message}", e)
                _errorMessage.value = e.message ?: "Failed to load project"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteProject() {
        _isDeleting.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                repository.deleteProject(projectId)
                _deleteSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete project"
                _isDeleting.value = false
            }
        }
    }
}

class ProjectDetailsViewModelFactory(
    private val projectId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectDetailsViewModel::class.java)) {
            val apiService = com.example.matchify.data.remote.ApiService.getInstance()
            val authPreferences = com.example.matchify.data.local.AuthPreferencesProvider.getInstance().get()
            val repository = PortfolioRepository(apiService.portfolioApi, authPreferences)
            @Suppress("UNCHECKED_CAST")
            return ProjectDetailsViewModel(repository, projectId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

