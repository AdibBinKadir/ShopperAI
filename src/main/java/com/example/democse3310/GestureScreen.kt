package com.example.democse3310

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GestureScreen(navController: NavHostController) {
    var message by remember { mutableStateOf("Touch the area below") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gesture Playground", style = MaterialTheme.typography.titleLarge)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .pointerInteropFilter { ev ->
                    message = when (ev.actionMasked) {
                        MotionEvent.ACTION_DOWN ->
                            "Touch Down @ (${ev.x.toInt()}, ${ev.y.toInt()})"
                        MotionEvent.ACTION_MOVE ->
                            "Move       @ (${ev.x.toInt()}, ${ev.y.toInt()})"
                        MotionEvent.ACTION_UP ->
                            "Touch Up"
                        MotionEvent.ACTION_CANCEL ->
                            "Cancel"
                        else -> "Other: ${ev.actionMasked}"
                    }
                    true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(message)
        }

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Main Page")
        }
    }
}