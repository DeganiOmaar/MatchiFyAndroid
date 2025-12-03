package com.example.matchify.ui.talent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.dto.ai.ProfileAnalysisResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * ViewModel pour l'analyse AI du profil
 * Clone exact de la logique iOS
 */
class AIProfileAnalysisViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {
    
    private val _analysis = MutableStateFlow<ProfileAnalysisResponseDto?>(null)
    val analysis: StateFlow<ProfileAnalysisResponseDto?> = _analysis
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // Cache for analysis result (like iOS)
    private var cachedAnalysis: ProfileAnalysisResponseDto? = null
    
    init {
        // Try to load cached result on init (like iOS)
        loadCachedAnalysis()
    }
    
    /**
     * Analyze profile with AI
     * Matches iOS AIProfileService.analyzeProfile()
     */
    fun analyzeProfile(onError: (String?) -> Unit) {
        if (_isLoading.value) return
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = aiRepository.analyzeProfile()
                _analysis.value = result
                cachedAnalysis = result
                _isLoading.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("AIProfileAnalysisVM", "Error analyzing profile: ${e.message}", e)
                _isLoading.value = false
                
                // Handle specific error cases (matching iOS)
                val message = when {
                    e.message?.contains("rate limit", ignoreCase = true) == true ->
                        "Limite d'analyses atteinte. Réessayez demain."
                    e.message?.contains("unavailable", ignoreCase = true) == true ->
                        "Le service d'analyse est temporairement indisponible. Réessayez plus tard."
                    e.message?.contains("quota", ignoreCase = true) == true ->
                        "Limite d'analyses atteinte. Réessayez demain."
                    else ->
                        "Erreur lors de l'analyse du profil. Réessayez plus tard."
                }
                
                _errorMessage.value = message
                onError(message)
            }
        }
    }
    
    /**
     * Load latest analysis
     * Matches iOS AIProfileService.getLatestAnalysis()
     */
    fun loadLatestAnalysis() {
        if (_isLoading.value) return
        
        // If we have cached analysis, use it first (like iOS)
        cachedAnalysis?.let {
            _analysis.value = it
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = aiRepository.getLatestProfileAnalysis()
                _analysis.value = result
                cachedAnalysis = result
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                
                // Don't show error for "not found" - just means no analysis yet (like iOS)
                if (e.message?.contains("not found", ignoreCase = true) == true ||
                    e.message?.contains("404", ignoreCase = true) == true) {
                    // No analysis found - this is OK, user can trigger one
                    return@launch
                }
                
                // Only log other errors, don't show to user on initial load
                Log.e("AIProfileAnalysisVM", "Error loading latest analysis: ${e.message}", e)
            }
        }
    }
    
    /**
     * Load cached analysis in background (non-blocking)
     * Matches iOS loadCachedAnalysis()
     */
    private fun loadCachedAnalysis() {
        viewModelScope.launch {
            loadLatestAnalysisSilently()
        }
    }
    
    /**
     * Load latest analysis silently without showing errors
     * Matches iOS loadLatestAnalysisSilently()
     */
    private suspend fun loadLatestAnalysisSilently() {
        try {
            val result = aiRepository.getLatestProfileAnalysis()
            _analysis.value = result
            cachedAnalysis = result
        } catch (e: Exception) {
            // Silently fail - user can trigger analysis manually
            // Check if it's a "not found" error - that's expected if no analysis exists yet
            if (e.message?.contains("not found", ignoreCase = true) == true ||
                e.message?.contains("404", ignoreCase = true) == true) {
                // This is expected - no analysis exists yet
                return
            }
            // For other errors, silently fail
            Log.d("AIProfileAnalysisVM", "No cached analysis available: ${e.message}")
        }
    }
}

/**
 * Factory for AIProfileAnalysisViewModel
 */
class AIProfileAnalysisViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIProfileAnalysisViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val aiRepository = AiRepository(apiService.aiApi)
            @Suppress("UNCHECKED_CAST")
            return AIProfileAnalysisViewModel(aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
