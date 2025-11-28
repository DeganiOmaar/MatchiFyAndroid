package com.example.matchify.ui.talent.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.dto.ai.ProfileAnalysisResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * ViewModel pour l'analyse AI du profil
 * Même comportement que iOS
 */
class AIProfileInsightsViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {
    
    private val _analysis = MutableStateFlow<ProfileAnalysisResponseDto?>(null)
    val analysis: StateFlow<ProfileAnalysisResponseDto?> = _analysis
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    /**
     * Charger la dernière analyse disponible
     */
    fun loadLatestAnalysis() {
        viewModelScope.launch {
            try {
                val latestAnalysis = aiRepository.getLatestProfileAnalysis()
                _analysis.value = latestAnalysis
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("AIProfileInsightsViewModel", "Error loading latest analysis: ${e.message}", e)
                // Ne pas afficher d'erreur si aucune analyse n'existe encore
                _analysis.value = null
            }
        }
    }
    
    /**
     * Analyser le profil avec AI
     */
    fun analyzeProfile(onError: (String?) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = aiRepository.analyzeProfile()
                _analysis.value = result
                _isLoading.value = false
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("AIProfileInsightsViewModel", "Error analyzing profile: ${e.message}", e)
                _isLoading.value = false
                val errorMsg = when {
                    e.message?.contains("quota", ignoreCase = true) == true -> 
                        "Quota d'analyse dépassé. Veuillez réessayer plus tard."
                    e.message?.contains("unavailable", ignoreCase = true) == true -> 
                        "Service AI temporairement indisponible."
                    else -> 
                        e.message ?: "Erreur lors de l'analyse du profil"
                }
                _errorMessage.value = errorMsg
                onError(errorMsg)
            }
        }
    }
}


