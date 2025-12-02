package com.example.democse3310

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.democse3310.ui.theme.DemoCSE3310Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoCSE3310Theme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginClick = { navController.navigate("home") },
                onSignUpClick = { navController.navigate("registration") }
            )
        }
        composable("registration") { RegistrationScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("text_search") { TextSearchScreen(navController) }
        composable("image_search") { ImageSearchScreen(navController) }
        composable("ai_assistant") { AiAssistantScreen(navController) }
        composable("budget_tracker") { BudgetTrackerScreen(navController) }
    }
}