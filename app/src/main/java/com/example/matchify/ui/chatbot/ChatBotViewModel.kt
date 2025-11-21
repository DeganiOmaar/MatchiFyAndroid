package com.example.matchify.ui.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatBotViewModel : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I'm the MatchiFy assistant. How can I help you today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    fun sendMessage(text: String) {
        val userMessage = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMessage
        
        // Simulate bot response
        viewModelScope.launch {
            delay(1000) // Simulate thinking time
            val botResponse = ChatMessage(
                text = generateBotResponse(text),
                isUser = false
            )
            _messages.value = _messages.value + botResponse
        }
    }
    
    private fun generateBotResponse(userInput: String): String {
        val lowerInput = userInput.lowercase()
        return when {
            lowerInput.contains("hello") || lowerInput.contains("hi") -> {
                "Hello! How can I assist you today?"
            }
            lowerInput.contains("help") -> {
                "I can help you with:\n• Understanding how to use the app\n• Creating missions or proposals\n• Managing your profile\n• Technical support"
            }
            lowerInput.contains("mission") -> {
                "To create a mission, go to the Missions tab and click the '+' button. Fill in the details and submit."
            }
            lowerInput.contains("proposal") -> {
                "To apply to a mission, view the mission details and click 'Apply'. Fill out the proposal form and submit."
            }
            else -> {
                "Thank you for your message. For more specific help, please contact our support team at support@matchify.com"
            }
        }
    }
}

class ChatBotViewModelFactory : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatBotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatBotViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

