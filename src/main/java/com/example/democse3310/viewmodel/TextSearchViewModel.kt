package com.example.democse3310.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.Product
import com.example.democse3310.repository.ProductRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TextSearchViewModel : ViewModel() {
    val query = mutableStateOf("")
    val products = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf("")
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("TextSearch", "Uncaught exception in coroutine", throwable)
        errorMessage.value = "Critical Error: ${throwable.javaClass.simpleName} - ${throwable.message}"
        products.clear()
        products.add(
            Product(
                id = "error_uncaught",
                name = "❌ Critical Error",
                description = "${throwable.javaClass.simpleName}: ${throwable.message}\n\nThe app caught this error to prevent a crash.",
                price = BigDecimal.ZERO,
                vendor = "Error Handler",
                imageUrl = "",
                productUrl = ""
            )
        )
        isLoading.value = false
    }

    fun searchProducts() {
        if (query.value.isBlank()) {
            errorMessage.value = "Please enter a search term"
            return
        }
        
        isLoading.value = true
        errorMessage.value = ""
        products.clear()
        
        viewModelScope.launch(exceptionHandler) {
            try {
                val results = ProductRepository.searchProducts(query.value)
                products.addAll(results)
                if (results.isEmpty()) {
                    errorMessage.value = "No products found for '${query.value}'"
                }
            } catch (e: Throwable) {
                android.util.Log.e("TextSearch", "Error in searchProducts", e)
                errorMessage.value = "Search failed: ${e.javaClass.simpleName} - ${e.message}"
                products.add(
                    Product(
                        id = "error_search",
                        name = "❌ Search Failed",
                        description = "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}",
                        price = BigDecimal.ZERO,
                        vendor = "Error",
                        imageUrl = "",
                        productUrl = ""
                    )
                )
            } finally {
                isLoading.value = false
            }
        }
    }
}