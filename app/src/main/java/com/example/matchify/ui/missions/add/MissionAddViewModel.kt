package com.example.matchify.ui.missions.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.data.remote.dto.CreateMissionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MissionAddViewModel(
    private val repository: MissionRepository
) : ViewModel() {

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val duration = MutableStateFlow("")
    val budget = MutableStateFlow("")
    val skillInput = MutableStateFlow("")
    val skills = MutableStateFlow<List<String>>(emptyList())

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

    fun createMission() {
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
                val request = CreateMissionRequest(
                    title = title.value,
                    description = description.value,
                    duration = duration.value,
                    budget = budgetValue,
                    skills = skills.value
                )

                repository.createMission(request)
                _isSaving.value = false
                _saveSuccess.value = true

                // Reset form
                resetForm()
            } catch (e: Exception) {
                _isSaving.value = false
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.MISSION_CREATE)
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
    }

    fun onSaveSuccessHandled() {
        _saveSuccess.value = false
    }
}


