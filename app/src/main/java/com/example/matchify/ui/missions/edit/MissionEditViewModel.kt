package com.example.matchify.ui.missions.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.remote.dto.mission.UpdateMissionRequest
import com.example.matchify.domain.model.Mission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MissionEditViewModel(
    private val repository: MissionRepository,
    private val mission: Mission
) : ViewModel() {

    val title = MutableStateFlow(mission.title)
    val description = MutableStateFlow(mission.description)
    val duration = MutableStateFlow(mission.duration)
    val budget = MutableStateFlow(mission.budget.toString())
    val skillInput = MutableStateFlow("")
    val skills = MutableStateFlow(mission.skills)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun addSkill() {
        val trimmed = skillInput.value.trim()
        if (trimmed.isNotEmpty() && skills.value.size < 10 && !skills.value.contains(trimmed)) {
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
            return title.value.isNotEmpty() &&
                    description.value.isNotEmpty() &&
                    duration.value.isNotEmpty() &&
                    filteredBudget.isNotEmpty() &&
                    skills.value.isNotEmpty() &&
                    filteredBudget.toIntOrNull() != null &&
                    skills.value.size <= 10
        }

    fun updateMission() {
        if (!isFormValid) {
            _errorMessage.value = "Veuillez remplir tous les champs requis."
            return
        }

        val filteredBudget = budget.value.filter { it.isDigit() }
        val budgetValue = filteredBudget.toIntOrNull()
            ?: run {
                _errorMessage.value = "Le budget doit être un nombre valide."
                return
            }

        _isSaving.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val request = UpdateMissionRequest(
                    title = title.value,
                    description = description.value,
                    duration = duration.value,
                    budget = budgetValue,
                    skills = skills.value
                )

                repository.updateMission(mission.missionId, request)
                _isSaving.value = false
                _saveSuccess.value = true
            } catch (e: Exception) {
                _isSaving.value = false
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.MISSION_UPDATE)
            }
        }
    }

    fun onSaveSuccessHandled() {
        _saveSuccess.value = false
    }
}


