package com.example.democse3310

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.democse3310.ui.theme.Spacing

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xl))
        Text("ShopperAI", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text("Your AI‑Powered Shopping Assistant", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.height(Spacing.xl))

        Text("Choose a search method", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(Spacing.md))

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ElevatedButton(
                onClick = { navController.navigate("text_search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Text Search", style = MaterialTheme.typography.titleMedium)
            }

            ElevatedButton(
                onClick = { navController.navigate("image_search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Reverse Image", style = MaterialTheme.typography.titleMedium)
            }

            ElevatedButton(
                onClick = { navController.navigate("ai_assistant") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("AI Assistant", style = MaterialTheme.typography.titleMedium)
            }

            ElevatedButton(
                onClick = { navController.navigate("budget_tracker") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Budget Tracker", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        OutlinedButton(
            onClick = {
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(48.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Logout", style = MaterialTheme.typography.labelSmall)
        }
    }
}