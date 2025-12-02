package com.example.democse3310.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.BuildConfig
import com.example.democse3310.data.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineExceptionHandler
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
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("AiAssistant", "Uncaught exception in coroutine", throwable)
        _messages.value = _messages.value + ChatMessage(
            "❌ Critical Error: ${throwable.javaClass.simpleName}\n${throwable.message}\n\nThe app caught this error to prevent a crash.",
            isUser = false
        )
        _isLoading.value = false
    }
    
    fun sendMessage(userMessage: String) {
        // Add user message immediately
        _messages.value = _messages.value + ChatMessage(userMessage, isUser = true)
        _isLoading.value = true
        
        viewModelScope.launch(exceptionHandler) {
            try {
                android.util.Log.d("AiAssistant", "=== Starting AI Assistant message ===")
                android.util.Log.d("AiAssistant", "User message: $userMessage")
                android.util.Log.d("AiAssistant", "API Key configured: ${BuildConfig.GEMINI_API_KEY.take(10)}...")
                
                // Initialize the model inside try-catch to catch initialization errors
                android.util.Log.d("AiAssistant", "Initializing GenerativeModel...")
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
                android.util.Log.d("AiAssistant", "GenerativeModel initialized successfully")
                
                // Create shopping-focused prompt
                val prompt = """
                    You are a helpful shopping assistant. The user is looking for product recommendations and shopping advice.
                    User query: $userMessage
                    
                    Provide helpful, concise advice. If they're looking for products, suggest what to search for and what features to consider.
                """.trimIndent()
                
                android.util.Log.d("AiAssistant", "Sending request to Gemini API...")
                val response = generativeModel.generateContent(prompt)
                android.util.Log.d("AiAssistant", "Received response from Gemini API")
                
                val aiResponse = response.text ?: "I'm sorry, I couldn't process that. Could you try rephrasing?"
                android.util.Log.d("AiAssistant", "Response text length: ${aiResponse.length}")
                
                // Add AI response
                _messages.value = _messages.value + ChatMessage(aiResponse, isUser = false)
            } catch (e: Throwable) {
                android.util.Log.e("AiAssistant", "=== ERROR in AI Assistant ===")
                android.util.Log.e("AiAssistant", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("AiAssistant", "Error message: ${e.message}")
                android.util.Log.e("AiAssistant", "Stack trace:", e)
                
                val errorDetails = when {
                    e.message?.contains("API key", ignoreCase = true) == true -> 
                        "API Key Error: ${e.message}"
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Network Error: Please check your internet connection. ${e.message}"
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Timeout Error: The request took too long. ${e.message}"
                    else -> 
                        "Error: ${e.javaClass.simpleName} - ${e.message ?: "Unknown error occurred"}"
                }
                
                _messages.value = _messages.value + ChatMessage(
                    "❌ Sorry, I encountered an error:\n\n$errorDetails\n\nPlease check your API key and internet connection.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}