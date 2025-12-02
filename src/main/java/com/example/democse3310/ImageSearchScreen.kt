package com.example.democse3310

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.democse3310.data.Product
import com.example.democse3310.repository.ProductRepository

@Composable
fun ImageSearchScreen(navController: NavController) {
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<Product>>(emptyList()) }
    val ctx = LocalContext.current

    // Gallery picker (returns Uri)
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
            selectedImagePath = uri?.toString()
            selectedBitmap = null
        }
    )

    // Camera preview (returns Bitmap)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bmp: Bitmap? ->
            selectedBitmap = bmp
            selectedImageUri = null
            selectedImagePath = if (bmp != null) "camera_preview" else null
        }
    )

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val largest = maxOf(w, h)
        if (largest <= maxDim) return bitmap
        val scale = maxDim.toFloat() / largest.toFloat()
        val newW = (w * scale).toInt()
        val newH = (h * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true)
    }

    fun uriToBase64(uri: Uri?): String? {
        if (uri == null) return null
        return try {
            ctx.contentResolver.openInputStream(uri).use { input ->
                val original = BitmapFactory.decodeStream(input)
                val scaled = original?.let { scaleBitmap(it, 1024) } ?: return null
                val baos = java.io.ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                val bytes = baos.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        return try {
            val scaled = scaleBitmap(bitmap, 1024)
            val baos = java.io.ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val bytes = baos.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Reverse Image Search", style = MaterialTheme.typography.headlineMedium)
                Text("Find products visually", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Per SRA section 4.7 - Upload or Capture options
        Text(
            "Upload an image or take a photo to find similar products",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Upload Image button
        Button(
            onClick = {
                pickImageLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image from Gallery")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Take Photo button
        Button(
            onClick = {
                takePictureLauncher.launch(null)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Take Photo with Camera")
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (selectedImagePath != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Image Selected: $selectedImagePath", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            results = emptyList()
                            coroutineScope.launch {
                                try {
                                    val b64 = when {
                                        selectedBitmap != null -> bitmapToBase64(selectedBitmap)
                                        selectedImageUri != null -> uriToBase64(selectedImageUri)
                                        else -> null
                                    }

                                    if (b64 != null) {
                                        val matches = ProductRepository.searchProductsByImage(b64)
                                        results = matches
                                    } else {
                                        results = emptyList()
                                    }
                                } catch (e: Exception) {
                                    results = emptyList()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Search for Similar Products")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Searching…", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (results.isNotEmpty()) {
                Text("Results:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(results) { p ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (p.productUrl.isNotBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(p.productUrl))
                                    ctx.startActivity(intent)
                                }
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(p.name, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(p.price.toPlainString(), style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(p.vendor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Note: Image recognition and product matching will be implemented with the Python backend using Computer Vision as specified in SRA section 4.5",
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