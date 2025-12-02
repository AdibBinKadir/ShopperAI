package com.example.democse3310.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ImageSearchViewModel : ViewModel() {
    val selectedImage = mutableStateOf<Bitmap?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    val searchQuery = mutableStateOf<String?>(null)
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("ImageSearch", "Uncaught exception in coroutine", throwable)
        errorMessage.value = "Critical Error: ${throwable.javaClass.simpleName} - ${throwable.message}"
        isLoading.value = false
    }
    
    fun analyzeImage(bitmap: Bitmap) {
        selectedImage.value = bitmap
        isLoading.value = true
        errorMessage.value = ""
        searchQuery.value = null
        
        viewModelScope.launch(exceptionHandler) {
            try {
                android.util.Log.d("ImageSearch", "=== Starting image analysis ===")
                android.util.Log.d("ImageSearch", "Image size: ${bitmap.width}x${bitmap.height}")
                android.util.Log.d("ImageSearch", "API Key configured: ${BuildConfig.GEMINI_API_KEY.take(10)}...")
                
                // Initialize Gemini model with vision capability
                android.util.Log.d("ImageSearch", "Initializing GenerativeModel with vision...")
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY
                )
                android.util.Log.d("ImageSearch", "GenerativeModel initialized successfully")
                
                // Create prompt for short product description
                val prompt = """
                    Describe this product image in 1-5 words maximum. Use only the most essential keywords for product search.
                    Examples:
                    - "wireless headphones"
                    - "blue running shoes"
                    - "laptop computer"
                    - "wooden desk"
                    
                    Respond with ONLY the keywords, nothing else.
                """.trimIndent()
                
                android.util.Log.d("ImageSearch", "Sending image to Gemini API...")
                val content = content {
                    image(bitmap)
                    text(prompt)
                }
                
                val response = generativeModel.generateContent(content)
                android.util.Log.d("ImageSearch", "Received response from Gemini API")
                
                val description = response.text?.trim() ?: ""
                android.util.Log.d("ImageSearch", "Image description: $description")
                
                if (description.isNotEmpty()) {
                    searchQuery.value = description
                    android.util.Log.d("ImageSearch", "Search query set: $description")
                } else {
                    errorMessage.value = "Could not identify the product in the image"
                }
                
            } catch (e: Throwable) {
                android.util.Log.e("ImageSearch", "=== ERROR in image analysis ===")
                android.util.Log.e("ImageSearch", "Error type: ${e.javaClass.simpleName}")
                android.util.Log.e("ImageSearch", "Error message: ${e.message}")
                android.util.Log.e("ImageSearch", "Stack trace:", e)
                
                val errorDetails = when {
                    e.message?.contains("API key", ignoreCase = true) == true -> 
                        "API Key Error: ${e.message}"
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Network Error: Please check your internet connection"
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Timeout Error: The request took too long"
                    else -> 
                        "Error: ${e.javaClass.simpleName} - ${e.message ?: "Unknown error"}"
                }
                
                errorMessage.value = errorDetails
            } finally {
                isLoading.value = false
            }
        }
    }
    
    fun clearImage() {
        selectedImage.value = null
        searchQuery.value = null
        errorMessage.value = ""
    }
}
