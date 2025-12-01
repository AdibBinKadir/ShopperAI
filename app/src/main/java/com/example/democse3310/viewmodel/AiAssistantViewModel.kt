package com.example.democse3310.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.ChatMessageEntity
import com.example.democse3310.repository.ChatRepository
import com.example.democse3310.repository.GenerativeAiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AiAssistantViewModel(
    private val chatRepository: ChatRepository,
    private val generativeAiRepository: GenerativeAiRepository = GenerativeAiRepository()
) : ViewModel() {
    
    private val _messages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<ChatMessageEntity>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val currentSessionId = UUID.randomUUID().toString()
    
    init {
        loadMessages()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.allMessages.collect { messageList ->
                _messages.value = messageList
            }
        }
    }
    
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            // Save user message
            val userMessage = ChatMessageEntity(
                text = text,
                isUser = true,
                sessionId = currentSessionId
            )
            chatRepository.insertMessage(userMessage)
            
            // Generate AI response
            _isLoading.value = true
            try {
                val response = generateAiResponse(text)
                
                // Save AI response
                val aiMessage = ChatMessageEntity(
                    text = response,
                    isUser = false,
                    sessionId = currentSessionId
                )
                chatRepository.insertMessage(aiMessage)
            } catch (e: Exception) {
                // Error handling
                val errorMessage = ChatMessageEntity(
                    text = "Sorry, I encountered an error: ${e.message}",
                    isUser = false,
                    sessionId = currentSessionId
                )
                chatRepository.insertMessage(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun generateAiResponse(userMessage: String): String {
        // TODO: Replace with actual Gemini API call
        return when {
            userMessage.contains("price", ignoreCase = true) -> 
                "I can help you compare prices across multiple vendors. Would you like to search for a specific product?"
            userMessage.contains("search", ignoreCase = true) -> 
                "You can search using text, upload an image, or describe what you're looking for. Which method would you prefer?"
            else -> 
                "I understand you're looking for: \"$userMessage\". Once the backend API is connected, I'll provide personalized recommendations based on your query."
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            chatRepository.deleteAllMessages()
        }
    }
    
    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
        }
    }
    
    fun addWelcomeMessage() {
        viewModelScope.launch {
            // Check if there are already messages
            if (_messages.value.isEmpty()) {
                val welcomeMessage = ChatMessageEntity(
                    text = "Hello! I'm your ShopperAI assistant. I can help you find products, compare prices, and answer questions about your shopping experience. What are you looking for today?",
                    isUser = false,
                    sessionId = currentSessionId
                )
                chatRepository.insertMessage(welcomeMessage)
            }
        }
    }
}
