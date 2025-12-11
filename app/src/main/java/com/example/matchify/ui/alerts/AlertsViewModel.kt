package com.example.matchify.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.remote.AlertRepository
import com.example.matchify.domain.model.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val repository: AlertRepository
) : ViewModel() {
    
    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isLoadingCount = MutableStateFlow(false)
    val isLoadingCount: StateFlow<Boolean> = _isLoadingCount.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun loadAlerts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val alertsList = repository.getAlerts()
                _alerts.value = alertsList
                loadUnreadCount()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erreur lors du chargement des alertes"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadUnreadCount() {
        viewModelScope.launch {
            _isLoadingCount.value = true
            try {
                val count = repository.getUnreadCount()
                _unreadCount.value = count
            } catch (e: Exception) {
                // Silently fail - count will remain at current value
            } finally {
                _isLoadingCount.value = false
            }
        }
    }
    
    fun markAsRead(alertId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(alertId)
                loadAlerts()
                loadUnreadCount()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
                loadAlerts()
                loadUnreadCount()
            } catch (e: Exception) {
                // Handle error silently
            }
        }
    }
}

class AlertsViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
            val apiService = com.example.matchify.data.remote.ApiService.getInstance()
            val repository = AlertRepository(apiService.alertApi)
            @Suppress("UNCHECKED_CAST")
            return AlertsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

