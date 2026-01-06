package com.example.matchify.ui.talent.scored

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.AiRepository
import com.example.matchify.data.remote.dto.ai.TalentScoredDtoMapper
import com.example.matchify.domain.model.TalentScored
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * ViewModel pour gérer l'affichage des talents scorés pour une mission
 */
class TalentScoredViewModel(
    private val aiRepository: AiRepository
) : ViewModel() {
    
    private val _talents = MutableStateFlow<List<TalentScored>>(emptyList())
    val talents: StateFlow<List<TalentScored>> = _talents
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    private val _httpErrorCode = MutableStateFlow<Int?>(null)
    val httpErrorCode: StateFlow<Int?> = _httpErrorCode
    
    /**
     * Charger les talents scorés pour une mission
     * 
     * @param missionId ID de la mission
     * @param limit Nombre max de talents à renvoyer (optionnel, défaut 50)
     * @param minScore Score minimum pour qu'un talent soit inclus (optionnel, défaut 0)
     */
    fun loadScoredTalents(
        missionId: String,
        limit: Int? = null,
        minScore: Int? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _httpErrorCode.value = null
            
            try {
                val dtos = aiRepository.getScoredTalentsForMission(
                    missionId = missionId,
                    limit = limit,
                    minScore = minScore
                )
                
                _talents.value = TalentScoredDtoMapper.toDomainList(dtos)
                _isLoading.value = false
                
            } catch (e: Exception) {
                _isLoading.value = false
                
                // Gérer les erreurs HTTP spécifiques (401, 403, 404)
                if (e is HttpException) {
                    val code = e.code()
                    _httpErrorCode.value = code
                    
                    // Message spécifique selon le code HTTP
                    _errorMessage.value = ErrorHandler.getHttpErrorMessage(
                        e,
                        ErrorContext.TALENT_MATCHING
                    )
                } else {
                    _errorMessage.value = ErrorHandler.getErrorMessage(
                        e,
                        ErrorContext.TALENT_MATCHING
                    )
                }
            }
        }
    }
    
    /**
     * Réinitialiser l'état
     */
    fun reset() {
        _talents.value = emptyList()
        _errorMessage.value = null
        _httpErrorCode.value = null
    }
}

