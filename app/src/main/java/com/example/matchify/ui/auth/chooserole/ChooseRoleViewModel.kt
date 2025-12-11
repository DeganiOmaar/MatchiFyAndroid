package com.example.matchify.ui.auth.chooserole

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class UserRole {
    Talent,
    Recruiter
}

class ChooseRoleViewModel : ViewModel() {

    private val _selectedRole = MutableStateFlow<UserRole?>(null)
    val selectedRole: StateFlow<UserRole?> = _selectedRole

    private val _goNext = MutableStateFlow(false)
    val goNext: StateFlow<Boolean> = _goNext

    fun selectRole(role: UserRole) {
        _selectedRole.value = role
    }

    fun continueNext() {
        if (_selectedRole.value != null) {
            _goNext.value = true
        }
    }
}