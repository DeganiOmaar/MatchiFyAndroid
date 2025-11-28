package com.example.matchify.ui.missions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.dto.ai.MissionFitResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

/**
 * ViewModel pour l'analyse de compatibilité mission-profil
 * Même comportement que iOS
 */
class MissionFitAnalysisViewModel(
    private val missionId: String,
    private val aiRepository: AiRepository
) : ViewModel() {
    
    private val _fitAnalysis = MutableStateFlow<MissionFitResponseDto?>(null)
    val fitAnalysis: StateFlow<MissionFitResponseDto?> = _fitAnalysis
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        loadFitAnalysis()
    }
    
    fun loadFitAnalysis() {
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val analysis = aiRepository.getMissionFit(missionId)
                _fitAnalysis.value = analysis
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("MissionFitAnalysisViewModel", "Error loading fit analysis: ${e.message}", e)
                _isLoading.value = false
                val errorMsg = when {
                    e.message?.contains("quota", ignoreCase = true) == true -> 
                        "Quota d'analyse dépassé. Veuillez réessayer plus tard."
                    e.message?.contains("unavailable", ignoreCase = true) == true -> 
                        "Service AI temporairement indisponible."
                    else -> 
                        e.message ?: "Erreur lors de l'analyse de compatibilité"
                }
                _errorMessage.value = errorMsg
            }
        }
    }
}

class MissionFitAnalysisViewModelFactory(
    private val missionId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MissionFitAnalysisViewModel::class.java)) {
            val apiService = ApiService.getInstance()
            val aiRepository = AiRepository(apiService.aiApi)
            @Suppress("UNCHECKED_CAST")
            return MissionFitAnalysisViewModel(missionId, aiRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


