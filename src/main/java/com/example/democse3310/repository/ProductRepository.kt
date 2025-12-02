package com.example.democse3310.repository

import com.example.democse3310.data.Product
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

object ProductRepository {
    
    // API key loaded from local.properties via BuildConfig
    private val geminiApiKey = com.example.democse3310.BuildConfig.GEMINI_API_KEY
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = geminiApiKey
    )
    
    suspend fun searchProducts(query: String): List<Product> = coroutineScope {
        generateAIProducts(query)
    }
    
    private suspend fun generateAIProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d("ProductRepo", "Generating AI products for: $query")
            
            val prompt = """
                Generate 15 realistic product listings for the search query: "$query"
                
                For each product, provide:
                - A realistic product name/title
                - A price (as a number, e.g., 299.99)
                - A vendor from this list: Amazon, Walmart, eBay, Target, Best Buy
                - Distribute products evenly across all 5 vendors (3 products per vendor)
                
                Format your response EXACTLY as JSON array:
                [
                  {
                    "name": "product name",
                    "price": 299.99,
                    "vendor": "Amazon"
                  }
                ]
                
                Return ONLY the JSON array, no other text or markdown.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            val text = response.text?.trim() ?: return@withContext emptyList()
            
            android.util.Log.d("ProductRepo", "AI Response: ${text.take(200)}")
            
            // Extract JSON from response - remove markdown formatting
            var jsonText = text
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // Find the JSON array in the text
            val arrayStart = jsonText.indexOf('[')
            val arrayEnd = jsonText.lastIndexOf(']')
            if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
                jsonText = jsonText.substring(arrayStart, arrayEnd + 1)
            }
            
            android.util.Log.d("ProductRepo", "Cleaned JSON: ${jsonText.take(200)}")
            
            val jsonArray = JSONArray(jsonText)
            val products = mutableListOf<Product>()
            
            for (i in 0 until jsonArray.length()) {
                try {
                    val item = jsonArray.getJSONObject(i)
                    val vendor = item.getString("vendor")
                    val name = item.getString("name")
                    val price = item.getDouble("price")
                    
                    val product = Product(
                        id = "${vendor.lowercase()}_ai_$i",
                        name = name,
                        description = vendor,
                        price = BigDecimal(price),
                        vendor = vendor,
                        imageUrl = "",
                        productUrl = generateProductUrl(vendor, query)
                    )
                    products.add(product)
                    android.util.Log.d("ProductRepo", "Added AI product: ${product.name} from ${product.vendor}")
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepo", "Error parsing AI item: ${e.message}")
                }
            }
            
            products
        } catch (e: Exception) {
            android.util.Log.e("ProductRepo", "Error generating AI products: ${e.message}")
            emptyList()
        }
    }
    
    private fun generateProductUrl(vendor: String, query: String): String {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        return when (vendor) {
            "Amazon" -> "https://www.amazon.com/s?k=$encodedQuery"
            "Walmart" -> "https://www.walmart.com/search?q=$encodedQuery"
            "eBay" -> "https://www.ebay.com/sch/i.html?_nkw=$encodedQuery"
            "Target" -> "https://www.target.com/s?searchTerm=$encodedQuery"
            "Best Buy" -> "https://www.bestbuy.com/site/searchpage.jsp?st=$encodedQuery"
            else -> "https://www.google.com/search?q=$encodedQuery"
        }
    }

    // Send a base64 image to the local backend and convert returned matches into Product objects
    suspend fun searchProductsByImage(imageBase64: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            // Build Retrofit pointing at the configured backend URL (set in local.properties -> BACKEND_API_URL)
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(com.example.democse3310.BuildConfig.BACKEND_URL.ifEmpty { "http://127.0.0.1:8000" })
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            interface ReverseImageService {
                @POST("reverse-image")
                suspend fun reverseImage(@Body body: Map<String, String>): ReverseImageResponse
            }

            data class Match(
                val name: String?,
                val price: String?,
                val image: String?,
                val url: String?,
                @SerializedName("source") val source: String?,
                val vendor: String?
            )

            data class ReverseImageResponse(val matches: List<Match>?)

            val service = retrofit.create(ReverseImageService::class.java)
            val resp = service.reverseImage(mapOf("image_base64" to imageBase64))
            val matches = resp.matches ?: emptyList()

            val products = mutableListOf<Product>()
            for ((i, item) in matches.withIndex()) {
                try {
                    val name = item.name ?: "Unknown Product"
                    val priceRaw = item.price ?: "0"
                    val vendor = item.vendor ?: item.source ?: "Backend"
                    val imageUrl = item.image ?: ""
                    val productUrl = item.url ?: ""

                    val priceClean = priceRaw.replace("$", "").replace(Regex("[^0-9.]"), "")
                    val price = try { priceClean.toDouble() } catch (e: Exception) { 0.0 }

                    products.add(
                        Product(
                            id = "backend_${i}",
                            name = name,
                            description = vendor,
                            price = java.math.BigDecimal.valueOf(price),
                            vendor = vendor,
                            imageUrl = imageUrl,
                            productUrl = productUrl
                        )
                    )
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepo", "Error parsing match item: ${e.message}")
                }
            }

            products
        } catch (e: Exception) {
            android.util.Log.e("ProductRepo", "searchProductsByImage failed: ${e.message}")
            emptyList()
        }
    }
}
