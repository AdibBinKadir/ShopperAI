package com.example.democse3310

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.democse3310.viewmodel.ImageSearchViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ImageSearchScreen(navController: NavController) {
    val viewModel: ImageSearchViewModel = viewModel()
    val context = LocalContext.current
    
    val selectedImage by viewModel.selectedImage
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val searchQuery by viewModel.searchQuery
    
    // Create a temporary file for camera
    val photoUri = remember {
        val photoFile = File(
            context.getExternalFilesDir(null),
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }
    
    // Navigate to text search when we have a query
    LaunchedEffect(searchQuery) {
        searchQuery?.let { query ->
            android.util.Log.d("ImageSearch", "Navigating to text_search with query: $query")
            navController.navigate("text_search/$query") {
                popUpTo("image_search") { inclusive = false }
            }
        }
    }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        try {
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap != null) {
                    android.util.Log.d("ImageSearch", "Image loaded from gallery, analyzing...")
                    viewModel.analyzeImage(bitmap)
                } else {
                    viewModel.errorMessage.value = "Failed to load image"
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ImageSearch", "Error loading image", e)
            viewModel.errorMessage.value = "Error loading image: ${e.message}"
        }
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            try {
                val inputStream = context.contentResolver.openInputStream(photoUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                if (bitmap != null) {
                    android.util.Log.d("ImageSearch", "Photo captured, analyzing...")
                    viewModel.analyzeImage(bitmap)
                } else {
                    viewModel.errorMessage.value = "Failed to capture photo"
                }
            } catch (e: Exception) {
                android.util.Log.e("ImageSearch", "Error processing photo", e)
                viewModel.errorMessage.value = "Error processing photo: ${e.message}"
            }
        } else {
            android.util.Log.d("ImageSearch", "Photo capture cancelled")
        }
    }
    
    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(photoUri)
        } else {
            viewModel.errorMessage.value = "Camera permission denied"
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
        
        // Upload or Capture options
        Text(
            "Upload an image or take a photo to find similar products",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Upload Image button
        Button(
            onClick = { 
                imagePickerLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Upload Image from Gallery")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Take Photo button
        Button(
            onClick = { 
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Take Photo with Camera")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Show loading indicator
        if (isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing image with Gemini AI...")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Generating search keywords...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Show error message
        if (errorMessage.isNotEmpty() && !isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "❌ Error",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.clearImage() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
        
        // Show selected image
        if (selectedImage != null && !isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Selected Image:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Image(
                        bitmap = selectedImage!!.asImageBitmap(),
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { viewModel.clearImage() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Image")
                    }
                }
            }
        }
        
        // Show placeholder when no image
        if (selectedImage == null && !isLoading && errorMessage.isEmpty()) {
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