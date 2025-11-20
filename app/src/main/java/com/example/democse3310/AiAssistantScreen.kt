package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@Composable
fun AiAssistantScreen(navController: NavController) {
    var messageInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()

    // Welcome message per SRA section 4.3
    LaunchedEffect(Unit) {
        messages.add(
            ChatMessage(
                "Hello! I'm your ShopperAI assistant. I can help you find products, compare prices, and answer questions about your shopping experience. What are you looking for today?",
                isUser = false
            )
        )
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
                Text("AI Assistant", style = MaterialTheme.typography.headlineMedium)
                Text("Chat about your shopping needs", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        // Chat messages - history recorded per SRA requirement
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatMessageBubble(message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                label = { Text("Ask me anything...") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )

            Button(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        // Add user message
                        messages.add(ChatMessage(messageInput, isUser = true))
                        
                        // TODO: Call Python API / Gemini LLM backend per SRA section 2.2 Objective 4
                        // Placeholder response for now
                        val response = when {
                            messageInput.contains("price", ignoreCase = true) -> 
                                "I can help you compare prices across multiple vendors. Would you like to search for a specific product?"
                            messageInput.contains("search", ignoreCase = true) -> 
                                "You can search using text, upload an image, or describe what you're looking for. Which method would you prefer?"
                            else -> 
                                "I understand you're looking for: \"$messageInput\". Once the backend API is connected, I'll provide personalized recommendations based on your query."
                        }
                        
                        messages.add(ChatMessage(response, isUser = false))
                        messageInput = ""
                    }
                },
                enabled = messageInput.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) 
                    MaterialTheme.colorScheme.primaryContainer 
                else 
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (message.isUser) "You" else "AI Assistant",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}