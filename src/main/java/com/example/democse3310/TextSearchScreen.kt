package com.example.democse3310

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.democse3310.viewmodel.TextSearchViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun TextSearchScreen(navController: NavController, vm: TextSearchViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Text Search", style = MaterialTheme.typography.headlineMedium)
                Text("Search by product name or description", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = vm.query.value,
            onValueChange = { vm.query.value = it },
            label = { Text("Search for a product") },
            placeholder = { Text("e.g., wireless headphones, laptop, running shoes") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { vm.searchProducts() }) {
                Text("Search")
            }
            
            TextButton(onClick = { 
                vm.query.value = ""
                vm.products.clear()
            }) {
                Text("Clear")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        if (vm.errorMessage.value.isNotEmpty()) {
            Text(
                vm.errorMessage.value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Text(
                "Searching Amazon, Walmart, eBay, Target, and Best Buy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (vm.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (vm.products.isEmpty() && vm.query.value.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No results found. Try a different search term.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(vm.products) { product ->
                    ProductListItem(product, vm.query.value)
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: com.example.democse3310.data.Product, searchQuery: String = "") {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                product.description, 
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Vendor: ${product.vendor}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "$%.2f".format(product.price.toDouble()), 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(onClick = { 
                    val url = if (product.productUrl.isNotBlank()) {
                        // Use direct product URL from API
                        product.productUrl
                    } else {
                        // Generate merchant search URL based on vendor
                        val encodedQuery = URLEncoder.encode(searchQuery.ifBlank { product.name }, StandardCharsets.UTF_8.toString())
                        when (product.vendor.lowercase()) {
                            "amazon" -> "https://www.amazon.com/s?k=$encodedQuery"
                            "ebay" -> "https://www.ebay.com/sch/i.html?_nkw=$encodedQuery"
                            "walmart" -> "https://www.walmart.com/search?q=$encodedQuery"
                            "best buy" -> "https://www.bestbuy.com/site/searchpage.jsp?st=$encodedQuery"
                            "target" -> "https://www.target.com/s?searchTerm=$encodedQuery"
                            "apple store", "apple" -> "https://www.apple.com/search/$encodedQuery"
                            "samsung" -> "https://www.samsung.com/us/search/?searchvalue=$encodedQuery"
                            "nike" -> "https://www.nike.com/w?q=$encodedQuery"
                            "adidas" -> "https://www.adidas.com/us/search?q=$encodedQuery"
                            "new balance" -> "https://www.newbalance.com/search/?text=$encodedQuery"
                            "logitech" -> "https://www.logitech.com/en-us/search.html?q=$encodedQuery"
                            "garmin" -> "https://www.garmin.com/en-US/search/?searchPhrase=$encodedQuery"
                            else -> "https://www.google.com/search?q=${URLEncoder.encode("${product.vendor} ${product.name}", StandardCharsets.UTF_8.toString())}"
                        }
                    }
                    
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }) {
                    Text("View")
                }
            }
        }
    }
}