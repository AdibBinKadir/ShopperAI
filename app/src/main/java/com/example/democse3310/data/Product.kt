package com.example.democse3310.data

import java.math.BigDecimal

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val vendor: String,
    val imageUrl: String,
    val productUrl: String
)