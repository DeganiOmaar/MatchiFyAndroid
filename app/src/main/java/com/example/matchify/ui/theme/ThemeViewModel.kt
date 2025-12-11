package com.example.matchify.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val prefs: com.example.matchify.data.local.AuthPreferences = AuthPreferencesProvider.getInstance().get()
) : ViewModel() {
    
    private val _currentTheme = MutableStateFlow<ThemeType>(ThemeType.SYSTEM)
    val currentTheme: StateFlow<ThemeType> = _currentTheme.asStateFlow()
    
    init {
        loadCurrentTheme()
        // Observe theme changes from preferences
        viewModelScope.launch {
            prefs.theme.collect { themeString ->
                if (themeString != null) {
                    try {
                        _currentTheme.value = ThemeType.valueOf(themeString)
                    } catch (e: IllegalArgumentException) {
                        _currentTheme.value = ThemeType.SYSTEM
                    }
                } else {
                    _currentTheme.value = ThemeType.SYSTEM
                }
            }
        }
    }
    
    private fun loadCurrentTheme() {
        viewModelScope.launch {
            // Load theme from preferences
            val themeString = prefs.getThemeValue()
            _currentTheme.value = if (themeString != null) {
                try {
                    ThemeType.valueOf(themeString)
                } catch (e: IllegalArgumentException) {
                    ThemeType.SYSTEM
                }
            } else {
                ThemeType.SYSTEM
            }
        }
    }
    
    fun setTheme(theme: ThemeType) {
        viewModelScope.launch {
            _currentTheme.value = theme
            // Save theme preference
            prefs.saveTheme(theme.name)
        }
    }
}

class ThemeViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

