package com.example.democse3310.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.Product
import com.example.democse3310.repository.ProductRepository
import kotlinx.coroutines.launch

class TextSearchViewModel : ViewModel() {
    val query = mutableStateOf("")
    val products = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun searchProducts() {
        if (query.value.isBlank()) {
            errorMessage.value = "Please enter a search term"
            return
        }
        
        isLoading.value = true
        errorMessage.value = ""
        products.clear()
        
        viewModelScope.launch {
            try {
                val results = ProductRepository.searchProducts(query.value)
                products.addAll(results)
                if (results.isEmpty()) {
                    errorMessage.value = "No products found for '${query.value}'"
                }
            } catch (e: Exception) {
                errorMessage.value = "Search failed: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}