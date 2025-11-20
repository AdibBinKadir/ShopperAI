package com.example.democse3310.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.repository.GenerativeAiRepository
import kotlinx.coroutines.launch

class AiAssistantViewModel : ViewModel() {
    private val repository = GenerativeAiRepository()
    val prompt = mutableStateOf("")
    val response = mutableStateOf("Ask me anything about your shopping needs!")
    val isLoading = mutableStateOf(false)

    fun sendPrompt() {
        if (prompt.value.isNotBlank()) {
            viewModelScope.launch {
                isLoading.value = true
                response.value = repository.generateResponse(prompt.value)
                isLoading.value = false
            }
        }
    }
}