package com.example.democse3310

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.example.democse3310.data.Product
import com.example.democse3310.data.featuredProducts
import com.example.democse3310.ProductCard

data class Retailer(
    val id: Int,
    val name: String,
    val website: String,
    val logo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val retailers = listOf(
        Retailer(1, "Walmart", "https://www.walmart.com", "https://logo.clearbit.com/walmart.com"),
        Retailer(2, "Amazon", "https://www.amazon.com", "https://logo.clearbit.com/amazon.com"),
        Retailer(3, "Best Buy", "https://www.bestbuy.com", "https://logo.clearbit.com/bestbuy.com"),
        Retailer(4, "eBay", "https://www.ebay.com", "https://logo.clearbit.com/ebay.com"),
        Retailer(5, "Target", "https://www.target.com", "https://logo.clearbit.com/target.com.au")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Sticky Search Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(50))
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                IconButton(onClick = { navController.navigate("image_search") }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Reverse Image Search")
                }
            }
        }

        // Retailer Quick Links
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Shop by Retailer", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(retailers) { retailer ->
                        val context = LocalContext.current
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(retailer.website))
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(retailer.logo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = retailer.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Featured Products
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text("Featured Products", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(featuredProducts) { product ->
                        ProductCard(product = product)
                    }
                }
            }
        }

        // Track Your Budget
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { /* TODO: Navigate to Budget Screen */ },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Track Your Budget", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Keep an eye on your spending.", style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }

        // Find Nearest Store
        item {
            OutlinedButton(
                onClick = { /* TODO: Find nearest store */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Find Nearest Store")
            }
        }
    }
}