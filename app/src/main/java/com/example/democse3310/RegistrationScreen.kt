package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegistrationScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var securityQuestion by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)
        Text("ShopperAI Registration", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName, 
            onValueChange = { fullName = it }, 
            label = { Text("Full Name (First and Last)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = email, 
            onValueChange = { email = it }, 
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = phoneNumber, 
            onValueChange = { phoneNumber = it }, 
            label = { Text("Phone Number (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = userId, 
            onValueChange = { userId = it }, 
            label = { Text("User ID (min 8 alphanumeric chars)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password, 
            onValueChange = { password = it }, 
            label = { Text("Password (8+ chars, 1 uppercase, 1 number)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = securityQuestion, 
            onValueChange = { securityQuestion = it }, 
            label = { Text("Security Question") },
            placeholder = { Text("e.g., What city were you born in?") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = securityAnswer, 
            onValueChange = { securityAnswer = it }, 
            label = { Text("Security Answer") },
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { 
                // Validation per SRA requirements
                when {
                    fullName.isBlank() -> errorMessage = "Full name is required"
                    email.isBlank() || !email.contains("@") -> errorMessage = "Valid email is required"
                    userId.length < 8 -> errorMessage = "User ID must be at least 8 characters"
                    password.length < 8 || !password.any { it.isUpperCase() } || !password.any { it.isDigit() } -> 
                        errorMessage = "Password must be 8+ chars with 1 uppercase and 1 number"
                    securityQuestion.isBlank() -> errorMessage = "Security question is required"
                    securityAnswer.isBlank() -> errorMessage = "Security answer is required"
                    else -> {
                        errorMessage = ""
                        // TODO: Save to database
                        navController.navigate("login") { 
                            popUpTo("registration") { inclusive = true } 
                        }
                    }
                }
            }) {
                Text("Submit")
            }
            
            OutlinedButton(onClick = { 
                fullName = ""
                email = ""
                phoneNumber = ""
                userId = ""
                password = ""
                securityQuestion = ""
                securityAnswer = ""
                errorMessage = ""
            }) {
                Text("Cancel")
            }
            
            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text("Exit")
            }
        }
    }
}