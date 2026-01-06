package com.example.matchify.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.common.ErrorContext
import com.example.matchify.common.ErrorHandler
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ZoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ZoomViewModel(
    private val repository: ZoomRepository
) : ViewModel() {
    
    private val _isZoomConnected = MutableStateFlow<Boolean?>(null)
    val isZoomConnected: StateFlow<Boolean?> = _isZoomConnected.asStateFlow()
    
    private val _zoomConnectUrl = MutableStateFlow<String?>(null)
    val zoomConnectUrl: StateFlow<String?> = _zoomConnectUrl.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        checkZoomStatus()
    }
    
    fun checkZoomStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val status = repository.getZoomStatus()
                _isZoomConnected.value = status.connected || status.zoomConnected == true
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
                _isZoomConnected.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun getZoomConnectUrl() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val response = repository.getZoomConnectUrl()
                _zoomConnectUrl.value = response.url
            } catch (e: Exception) {
                _errorMessage.value = ErrorHandler.getErrorMessage(e, ErrorContext.GENERAL)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ZoomViewModelFactory(
    private val repository: ZoomRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ZoomViewModel(repository) as T
    }
}

