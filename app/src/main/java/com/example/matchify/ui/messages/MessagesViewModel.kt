package com.example.matchify.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ConversationRepository
import com.example.matchify.domain.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(
    private val repository: ConversationRepository
) : ViewModel() {
    
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
    
    init {
        loadConversations()
    }
    
    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val conversationsList = repository.getConversations()
                
                // Load unread count for each conversation (matching iOS behavior)
                val conversationsWithCounts = conversationsList.map { conversation ->
                    try {
                        val unreadCount = repository.getConversationUnreadCount(conversation.conversationId)
                        conversation.copy(unreadCount = unreadCount)
                    } catch (e: Exception) {
                        // If fetching unread count fails, set to 0
                        conversation.copy(unreadCount = 0)
                    }
                }
                
                _conversations.value = conversationsWithCounts
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshUnreadCounts() {
        viewModelScope.launch {
            val currentConversations = _conversations.value
            
            // Reload unread count for each conversation
            val updatedConversations = currentConversations.map { conversation ->
                try {
                    val unreadCount = repository.getConversationUnreadCount(conversation.conversationId)
                    conversation.copy(unreadCount = unreadCount)
                } catch (e: Exception) {
                    conversation.copy(unreadCount = 0)
                }
            }
            
            _conversations.value = updatedConversations
        }
    }
    
    fun updateConversationUnreadCount(conversationId: String, count: Int) {
        val currentConversations = _conversations.value
        val updatedConversations = currentConversations.map { conversation ->
            if (conversation.conversationId == conversationId) {
                conversation.copy(unreadCount = count)
            } else {
                conversation
            }
        }
        _conversations.value = updatedConversations
    }
}

class MessagesViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessagesViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val repository = ConversationRepository(apiService, prefs)
            @Suppress("UNCHECKED_CAST")
            return MessagesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

