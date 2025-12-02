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
            Product(1, "Vintage Leather Jacket", "$120.00", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", "https://www.amazon.com/s?k=vintage+leather+jacket"),
            Product(2, "Classic Blue Jeans", "$60.00", "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg", "https://www.amazon.com/s?k=classic+blue+jeans"),
            Product(3, "White Cotton T-Shirt", "$25.00", "https://fakestoreapi.com/img/71YXzeOuslL._AC_UY879_.jpg", "https://www.amazon.com/s?k=white+cotton+tshirt")
        )
    }
}
