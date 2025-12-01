package com.example.democse3310.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.Product
import com.example.democse3310.repository.GenerativeAiRepository
import kotlinx.coroutines.launch

class ImageSearchViewModel : ViewModel() {

    private val generativeAiRepository = GenerativeAiRepository()

    val products = mutableStateOf<List<Product>>(emptyList())
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)
    val selectedImageBitmap = mutableStateOf<Bitmap?>(null)

    fun searchByImage(image: Bitmap) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            products.value = emptyList() // Clear previous results
            try {
                products.value = generativeAiRepository.findSimilarProducts(image)
            } catch (e: Exception) {
                error.value = "An error occurred: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
