package com.example.matchify.ui.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchify.data.local.AuthPreferencesProvider
import com.example.matchify.data.remote.ApiService
import com.example.matchify.data.remote.ConversationRepository
import com.example.matchify.data.remote.MissionRepository
import com.example.matchify.domain.model.Conversation
import com.example.matchify.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversationChatViewModel(
    private val conversationId: String,
    private val repository: ConversationRepository,
    private val missionRepository: MissionRepository
) : ViewModel() {
    
    // ... existing flows ...
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
    
    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation: StateFlow<Conversation?> = _conversation.asStateFlow()
    
    private val _mission = MutableStateFlow<com.example.matchify.domain.model.Mission?>(null)
    val mission: StateFlow<com.example.matchify.domain.model.Mission?> = _mission.asStateFlow()
    
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()
    
    val isRecruiter: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "recruiter"
        }
        
    val isTalent: Boolean
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentRole.value == "talent"
        }
    
    val currentUserId: String?
        get() {
            val prefs = AuthPreferencesProvider.getInstance().get()
            return prefs.currentUser.value?.id
        }
        
    val shouldShowApproveButton: StateFlow<Boolean> = MutableStateFlow(false).also { flow ->
        viewModelScope.launch {
            _mission.collect { mission ->
                // Show only for Recruiter AND when mission is completed
                val show = isRecruiter && mission != null && mission.status == "completed"
                flow.value = show
            }
        }
    }
    
    init {
        loadConversation()
        loadMessages()
        markAsRead() // Mark conversation as read when opened (matching iOS behavior)
    }
    
    fun markAsRead() {
        viewModelScope.launch {
            try {
                repository.markConversationAsRead(conversationId)
                // Note: Badge counts will be refreshed when returning to messages_list
            } catch (e: Exception) {
                // Silently fail - marking as read is not critical
                e.printStackTrace()
            }
        }
    }
    
    fun loadConversation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val conv = repository.getConversationById(conversationId)
                _conversation.value = conv
                
                // Load mission if exists
                if (!conv.missionId.isNullOrBlank()) {
                    loadMission(conv.missionId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadMission(missionId: String) {
        try {
            val loadedMission = missionRepository.getMissionById(missionId)
            _mission.value = loadedMission
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun loadMessages() {
        viewModelScope.launch {
            try {
                val messagesList = repository.getConversationMessages(conversationId)
                _messages.value = messagesList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun sendMessage() {
        val text = messageText.value.trim()
        if (text.isEmpty() || _isSending.value) return
        
        _isSending.value = true
        val messageToSend = text
        _messageText.value = ""
        
        viewModelScope.launch {
            try {
                val sentMessage = repository.sendMessage(conversationId, messageToSend)
                _messages.value = _messages.value + sentMessage
                
                // Refresh messages to get updated list
                loadMessages()
            } catch (e: Exception) {
                _messageText.value = messageToSend
                e.printStackTrace()
            } finally {
                _isSending.value = false
            }
        }
    }
    
    fun updateMessageText(text: String) {
        _messageText.value = text
    }
    
    fun isMessageFromCurrentUser(message: Message): Boolean {
        return message.senderId == currentUserId
    }
    
    fun refreshMessages() {
        loadMessages()
    }
    
    fun refreshMission() {
        _conversation.value?.missionId?.let { 
            viewModelScope.launch { loadMission(it) } 
        }
    }
    
    // Deliverable submission methods
    fun uploadDeliverable(fileUri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val (message, deliverable) = repository.uploadDeliverable(conversationId, fileUri, context)
                _messages.value = _messages.value + message
                loadMessages() // Refresh to get the complete message list
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: Expose error state
            } finally {
                _isSending.value = false
            }
        }
    }
    
    fun submitLink(url: String, title: String?) {
        viewModelScope.launch {
            _isSending.value = true
            try {
                val (message, deliverable) = repository.submitLink(conversationId, url, title)
                _messages.value = _messages.value + message
                loadMessages() // Refresh to get the complete message list
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: Expose error state
            } finally {
                _isSending.value = false
            }
        }
    }
    
    fun approveDeliverable(deliverableId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateDeliverableStatus(deliverableId, "approved")
                loadMessages() // Refresh messages to show updated status
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun requestChanges(deliverableId: String, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateDeliverableStatus(deliverableId, "revision_requested", reason)
                loadMessages() // Refresh messages to show updated status
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ConversationChatViewModelFactory(
    private val conversationId: String
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationChatViewModel::class.java)) {
            val prefs = AuthPreferencesProvider.getInstance().get()
            val apiService = ApiService.getInstance()
            val repository = ConversationRepository(apiService, prefs)
            val missionRepository = MissionRepository(apiService.missionApi, prefs)
            @Suppress("UNCHECKED_CAST")
            return ConversationChatViewModel(conversationId, repository, missionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

