package com.example.democse3310

import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.democse3310.data.Product
import com.example.democse3310.ui.theme.Spacing
import java.math.BigDecimal

@Composable
fun ImageSearchScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var mockResults by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Gallery picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        // Clear previous mock results when a new image is picked
        mockResults = emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Reverse Image Search", style = MaterialTheme.typography.headlineMedium)
                Text("Find products visually", style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        Text(
            "Upload an image or take a photo to find similar products",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Upload Image button
        Button(
            onClick = {
                galleryLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Upload Image from Gallery")
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Take Photo button - still a stub (Camera implementation omitted)
        Button(
            onClick = {
                // For now, navigate to Camera screen (stub)
                navController.navigate("camera")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Take Photo with Camera")
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        if (selectedImageUri != null) {
            Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    // Image preview using ImageView inside AndroidView
                    AndroidView(
                        factory = { ctx ->
                            ImageView(ctx).apply {
                                adjustViewBounds = true
                                scaleType = ImageView.ScaleType.CENTER_CROP
                                layoutParams = android.view.ViewGroup.LayoutParams(
                                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            }
                        },
                        update = { imageView ->
                            imageView.setImageURI(selectedImageUri)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Text("Image Selected: ${selectedImageUri}", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Button(
                        onClick = {
                            // Produce mock results for demo
                            mockResults = listOf(
                                Product(
                                    id = "p1",
                                    name = "Wireless Headphones",
                                    description = "Over-ear, noise-cancelling",
                                    price = BigDecimal("79.99"),
                                    vendor = "Vendor A",
                                    imageUrl = "",
                                    productUrl = ""
                                ),
                                Product(
                                    id = "p2",
                                    name = "Bluetooth Earbuds",
                                    description = "Compact true wireless earbuds",
                                    price = BigDecimal("49.99"),
                                    vendor = "Vendor B",
                                    imageUrl = "",
                                    productUrl = ""
                                ),
                                Product(
                                    id = "p3",
                                    name = "Over-ear Studio Headphones",
                                    description = "High-fidelity sound",
                                    price = BigDecimal("129.99"),
                                    vendor = "Vendor C",
                                    imageUrl = "",
                                    productUrl = ""
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Search for Similar Products")
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Display mock results if available
            if (mockResults.isNotEmpty()) {
                Text("Results", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(Spacing.sm))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    items(mockResults) { product ->
                        ProductListItem(product = product)
                    }
                }
            } else {
                Text(
                    "Note: Image recognition and product matching will be implemented with the Python backend. For now, use 'Search' to see mock results.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No image selected",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}