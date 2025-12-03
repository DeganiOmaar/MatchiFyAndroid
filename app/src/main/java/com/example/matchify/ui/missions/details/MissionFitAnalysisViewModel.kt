package com.example.matchify.ui.missions.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.dto.ai.MissionFitResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MissionFitAnalysisViewModel(
    private val repository: AiRepository
) : ViewModel() {
    
    private val _analysis = MutableStateFlow<MissionFitResponseDto?>(null)
    val analysis: StateFlow<MissionFitResponseDto?> = _analysis.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun analyzeMissionFit(missionId: String) {
        if (_isLoading.value) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = repository.analyzeMissionFit(missionId)
                _analysis.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors de l'analyse de la mission"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun reset() {
        _analysis.value = null
        _errorMessage.value = null
        _isLoading.value = false
    }
}