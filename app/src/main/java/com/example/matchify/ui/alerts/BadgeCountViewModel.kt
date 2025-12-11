package com.example.matchify.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.AlertRepository
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ProposalRepository
import com.example.matchify.data.remote.ConversationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class BadgeCountViewModel(
    private val alertRepository: AlertRepository,
    private val proposalRepository: ProposalRepository,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    
    private val _alertsUnreadCount = MutableStateFlow(0)
    val alertsUnreadCount: StateFlow<Int> = _alertsUnreadCount.asStateFlow()
    
    private val _proposalsUnreadCount = MutableStateFlow(0)
    val proposalsUnreadCount: StateFlow<Int> = _proposalsUnreadCount.asStateFlow()
    
    private val _conversationsWithUnreadCount = MutableStateFlow(0)
    val conversationsWithUnreadCount: StateFlow<Int> = _conversationsWithUnreadCount.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    init {
        loadCounts()
        startPeriodicRefresh()
    }
    
    fun loadCounts() {
        viewModelScope.launch {
            // Load alerts count
            try {
                val count = alertRepository.getUnreadCount()
                _alertsUnreadCount.value = count
            } catch (e: Exception) {
                // Silently fail
            }
            
            // Load proposals count (only for recruiters)
            if (isRecruiter) {
                try {
                    // Note: This would need to be implemented in ProposalRepository
                    // For now, we'll leave it at 0
                    _proposalsUnreadCount.value = 0
                } catch (e: Exception) {
                    // Silently fail
                }
            }
            
            // Load conversations with unread count
            try {
                val count = conversationRepository.getConversationsWithUnreadCount()
                _conversationsWithUnreadCount.value = count
            } catch (e: Exception) {
                // Silently fail
                _conversationsWithUnreadCount.value = 0
            }
        }
    }
    
    private fun startPeriodicRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30000) // Refresh every 30 seconds
                loadCounts()
            }
        }
    }
}

class BadgeCountViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BadgeCountViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val alertRepository = AlertRepository(apiService.alertApi)
            val proposalRepository = ProposalRepository(apiService, prefs)
            val conversationRepository = ConversationRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return BadgeCountViewModel(alertRepository, proposalRepository, conversationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

