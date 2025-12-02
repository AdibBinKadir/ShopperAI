package com.example.democse3310.data

import java.math.BigDecimal

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val imageUrl: String,
    val productUrl: String // New field for the click destination
)

val featuredProducts = listOf(
    Product(
        1, "Noise Cancelling Headphones", "$129.99", 
        "https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg?auto=compress&cs=tinysrgb&w=600",
        "https://www.amazon.com/s?k=wireless+headphones"
    ),
    Product(
        2, "RGB Mechanical Keyboard", "$89.99", 
        "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?auto=format&fit=crop&w=600&q=80",
        "https://www.amazon.com/s?k=mechanical+keyboard"
    ),
    Product(
        3, "Pro Mirrorless Camera", "$450.00", 
        "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=600&q=80",
        "https://www.amazon.com/s?k=digital+camera"
    ),
    Product(
        4, "4K Ultra Monitor", "$299.99", 
        "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=600&q=80",
        "https://www.amazon.com/s?k=gaming+monitor"
    ),
    Product(
        5, "Smart Series Watch", "$199.99", 
        "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?auto=format&fit=crop&w=600&q=80",
        "https://www.amazon.com/s?k=smart+watch"
    )
)

val savedItems = listOf(
    Product(
        101, "Leather Backpack", "$59.99", 
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=400&q=80",
        "https://www.amazon.com/s?k=leather+backpack"
    ),
    Product(
        102, "Running Shoes", "$120.00", 
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=400&q=80",
        "https://www.amazon.com/s?k=running+shoes"
    ),
    Product(
        103, "Modern Sunglasses", "$25.00", 
        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=400&q=80",
        "https://www.amazon.com/s?k=sunglasses"
    )
)
