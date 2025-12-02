package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to ShopperAI", style = MaterialTheme.typography.headlineLarge)
        Text("Your AI-Powered Shopping Assistant", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(48.dp))
        
        // Three main search methods per SRA section 1
        Text("Choose Your Search Method:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { navController.navigate("text_search") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Text-Based Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("image_search") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Reverse Image Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("ai_assistant") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("AI Assistant Chat")
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("map") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Find Nearby Stores")
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("budget_tracker") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Budget Tracker")
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedButton(
            onClick = { 
                navController.navigate("login") { 
                    popUpTo("home") { inclusive = true } 
                } 
            }
        ) {
            Text("Logout")
        }
    }
}