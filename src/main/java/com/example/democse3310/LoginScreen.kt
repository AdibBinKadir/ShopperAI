package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var userIdOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("ShopperAI Login", style = MaterialTheme.typography.headlineLarge)
        Text("Your AI-Powered Shopping Assistant", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = userIdOrEmail, 
            onValueChange = { userIdOrEmail = it }, 
            label = { Text("User ID or Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { 
                // TODO: Validate with database
                if (userIdOrEmail.isNotBlank() && password.isNotBlank()) {
                    navController.navigate("home") { 
                        popUpTo("login") { inclusive = true } 
                    }
                } else {
                    errorMessage = "Incorrect username or password"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = { 
            // TODO: Navigate to password recovery
            errorMessage = "Password recovery coming soon"
        }) {
            Text("Forgot Password?")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextButton(onClick = { navController.navigate("registration") }) {
            Text("New user? Register here")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(onClick = { 
            // Exit application
            errorMessage = ""
        }) {
            Text("Exit")
        }
    }
}