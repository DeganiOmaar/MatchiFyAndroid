package com.example.matchify.ui.missions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.common.ValidationErrorResponse
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.remote.dto.mission.CreateMissionRequest
import com.example.matchify.data.realtime.MissionRealtimeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MissionAddViewModel(
    private val repository: MissionRepository,
    private val realtimeClient: MissionRealtimeClient
) : ViewModel() {

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val duration = MutableStateFlow("")
    val budget = MutableStateFlow("")
    val skillInput = MutableStateFlow("")
    val skills = MutableStateFlow<List<String>>(emptyList())
    val experienceLevel = MutableStateFlow<String?>(null) // ENTRY, INTERMEDIATE, or EXPERT

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // Erreurs de validation par champ (fieldErrors du backend)
    private val _fieldErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val fieldErrors: StateFlow<Map<String, String>> = _fieldErrors

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess
    
    /**
     * Récupère le message d'erreur pour un champ spécifique
     */
    fun getFieldError(fieldName: String): String? {
        return _fieldErrors.value[fieldName]
    }

    fun addSkill() {
        val trimmed = skillInput.value.trim()
        if (trimmed.isNotEmpty() && !skills.value.contains(trimmed)) {
            skills.value = skills.value + trimmed
            skillInput.value = ""
        }
    }

    fun removeSkill(skill: String) {
        skills.value = skills.value.filter { it != skill }
    }

    val isFormValid: Boolean
        get() {
            val filteredBudget = budget.value.filter { it.isDigit() }
            return title.value.trim().isNotEmpty() &&
                    description.value.trim().isNotEmpty() &&
                    duration.value.trim().isNotEmpty() &&
                    filteredBudget.isNotEmpty() &&
                    skills.value.isNotEmpty() &&
                    filteredBudget.toIntOrNull() != null &&
                    experienceLevel.value != null
        }

    fun createMission() {
        // Validation détaillée avec messages spécifiques
        when {
            title.value.trim().isEmpty() -> {
                _errorMessage.value = "Le titre de la mission est requis."
                return
            }
            description.value.trim().isEmpty() -> {
                _errorMessage.value = "La description est requise."
                return
            }
            duration.value.trim().isEmpty() -> {
                _errorMessage.value = "La durée est requise."
                return
            }
            budget.value.trim().isEmpty() -> {
                _errorMessage.value = "Le budget est requis."
                return
            }
            skills.value.isEmpty() -> {
                _errorMessage.value = "Au moins une compétence est requise."
                return
            }
            experienceLevel.value == null -> {
                _errorMessage.value = "Le niveau d'expérience est requis."
                return
            }
        }

        val filteredBudget = budget.value.filter { it.isDigit() }
        val budgetValue = filteredBudget.toIntOrNull()
            ?: run {
                _errorMessage.value = "Le budget doit être un nombre valide."
                return
            }
        
        if (budgetValue <= 0) {
            _errorMessage.value = "Le budget doit être supérieur à 0."
            return
        }

        _isSaving.value = true
        _errorMessage.value = null
        _fieldErrors.value = emptyMap()

        viewModelScope.launch {
            try {
                val request = CreateMissionRequest(
                    title = title.value.trim(),
                    description = description.value.trim(),
                    duration = duration.value.trim(),
                    budget = budgetValue,
                    skills = skills.value,
                    experienceLevel = experienceLevel.value!! // Already validated above
                )

                repository.createMission(request)
                _isSaving.value = false
                _saveSuccess.value = true

                // Reset form
                resetForm()
            } catch (e: Exception) {
                _isSaving.value = false
                
                // Extraire les erreurs de validation structurées si c'est une HttpException
                if (e is HttpException) {
                    val validationErrors = ErrorHandler.extractValidationErrors(e)
                    if (validationErrors != null && !validationErrors.fieldErrors.isNullOrEmpty()) {
                        // Afficher les erreurs par champ
                        _fieldErrors.value = validationErrors.fieldErrors
                        // Message général si disponible
                        _errorMessage.value = validationErrors.message
                    } else {
                        // Message d'erreur générique
                        _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.MISSION_CREATE)
                        _fieldErrors.value = emptyMap()
                    }
                } else {
                    _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.MISSION_CREATE)
                    _fieldErrors.value = emptyMap()
                }
            }
        }
    }

    private fun resetForm() {
        title.value = ""
        description.value = ""
        duration.value = ""
        budget.value = ""
        skillInput.value = ""
        skills.value = emptyList()
        experienceLevel.value = null
        _fieldErrors.value = emptyMap()
        _errorMessage.value = null
    }

    fun onSaveSuccessHandled() {
        _saveSuccess.value = false
    }
}


