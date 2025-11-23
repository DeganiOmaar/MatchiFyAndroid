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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

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
                val conversations = conversationRepository.getConversations()
                val prefs = AuthPreferencesProvider.getInstance().get()
                val lastViewedTimestamp = prefs.getLastMessagesViewedValue()
                
                // Si l'utilisateur n'a jamais vu les messages, compter toutes les conversations avec des messages
                val lastViewedDate = if (lastViewedTimestamp != null) {
                    try {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                        dateFormat.parse(lastViewedTimestamp)
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
                
                // Compter les conversations avec des messages plus récents que la dernière consultation
                val unreadCount = conversations.count { conversation ->
                    conversation.lastMessageAt?.let { lastMessageAt ->
                        try {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                            val messageDate = dateFormat.parse(lastMessageAt)
                            
                            // Si on a une date de dernière consultation, comparer
                            // Sinon, compter toutes les conversations avec des messages
                            if (lastViewedDate != null && messageDate != null) {
                                messageDate.after(lastViewedDate)
                            } else if (messageDate != null) {
                                // Si pas de date de consultation, compter toutes les conversations avec messages
                                true
                            } else {
                                false
                            }
                        } catch (e: Exception) {
                            false
                        }
                    } ?: false
                }
                
                _conversationsWithUnreadCount.value = unreadCount
            } catch (e: Exception) {
                // Silently fail
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
    
    fun refreshUnreadCount() {
        loadCounts()
    }
}

class BadgeCountViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
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

