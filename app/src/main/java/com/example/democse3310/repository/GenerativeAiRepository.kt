package com.example.democse3310.repository

import android.graphics.Bitmap
import com.example.democse3310.data.Product

class GenerativeAiRepository {
    // private val generativeModel = GenerativeModel(...) // <-- DISABLED

    suspend fun generateResponse(prompt: String): String {
        // return try { ... } // <-- DISABLED
        return "AI Assistant is temporarily disabled."
    }

    // TODO: Implement actual reverse image search logic
    suspend fun findSimilarProducts(image: Bitmap): List<Product> {
        // For now, return a hardcoded list of products for demonstration purposes
        return listOf(
            Product("1", "Vintage Leather Jacket", "A stylish vintage leather jacket.", 120.00, "https://example.com/jacket.jpg"),
            Product("2", "Classic Blue Jeans", "Comfortable and durable blue jeans.", 60.00, "https://example.com/jeans.jpg"),
            Product("3", "White Cotton T-Shirt", "A soft and breathable cotton t-shirt.", 25.00, "https://example.com/tshirt.jpg")
        )
    }
}