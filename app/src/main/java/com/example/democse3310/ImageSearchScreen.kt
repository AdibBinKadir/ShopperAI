package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ImageSearchScreen(navController: NavController) {
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    
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
                // TODO: Implement image picker
                selectedImagePath = "image_selected.jpg"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image from Gallery")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Take Photo button
        Button(
            onClick = { 
                // TODO: Implement camera capture
                selectedImagePath = "photo_taken.jpg"
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
                            // TODO: Process image with Computer Vision per SRA section 4.5
                            // Extract visual features and search database
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Search for Similar Products")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Note: Image recognition and product matching will be implemented with the Python backend using Computer Vision as specified in SRA section 4.5",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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