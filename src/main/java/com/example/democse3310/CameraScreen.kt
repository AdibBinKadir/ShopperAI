package com.example.democse3310

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun CameraScreen(navController: NavHostController) {
    Text("Camera screen")
    Button(onClick = { navController.popBackStack() }) {
        Text("Back to Main Page")
    }
}