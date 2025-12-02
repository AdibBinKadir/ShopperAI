package com.example.democse3310.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.democse3310.data.Product


class TextSearchViewModel : ViewModel() {
    val query = mutableStateOf("")
    val products = mutableStateListOf<Product>()
    val isLoading = mutableStateOf(false)

    fun searchProducts() {
        isLoading.value = true
        products.clear()
        // This is where you will eventually call your Python backend
        products.addAll(
            listOf(
                Product(1, "Sample Laptop", "$999.99", "", ""),
                Product(2, "Sample Keyboard", "$79.99", "", "")
            )
        )
        isLoading.value = false
    }
}