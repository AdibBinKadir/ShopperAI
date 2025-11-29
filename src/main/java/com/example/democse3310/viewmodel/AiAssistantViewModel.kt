package com.example.democse3310.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiAssistantViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                "Hello! I'm your ShopperAI assistant powered by Gemini. I can help you find products, compare prices, and answer questions about shopping. What are you looking for today?",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // TODO: Replace with your actual Gemini API key
    // Add it to local.properties as GEMINI_API_KEY=your_key_here
    // Then access it via BuildConfig.GEMINI_API_KEY
    private val apiKey = "AIzaSyDSInoqr9catuTB83T0iBJG4mKD5u0S18g"
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )
    
    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            // Add user message
            _messages.value = _messages.value + ChatMessage(userMessage, isUser = true)
            _isLoading.value = true
            
            try {
                // Create shopping-focused prompt
                val prompt = """
                    You are a helpful shopping assistant. The user is looking for product recommendations and shopping advice.
                    User query: $userMessage
                    
                    Provide helpful, concise advice. If they're looking for products, suggest what to search for and what features to consider.
                """.trimIndent()
                
                val response = generativeModel.generateContent(prompt)
                val aiResponse = response.text ?: "I'm sorry, I couldn't process that. Could you try rephrasing?"
                
                // Add AI response
                _messages.value = _messages.value + ChatMessage(aiResponse, isUser = false)
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    "Sorry, I'm having trouble connecting. Error: ${e.message}. Make sure you've added your Gemini API key!",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}