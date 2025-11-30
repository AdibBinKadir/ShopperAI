package com.example.democse3310

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.democse3310.ui.theme.*

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppWhite)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        // --- App Title ---
        Text(
            text = "ShopperAI",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = BluePrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Welcome back",
            fontSize = 18.sp,
            color = MediumGray
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- Form Container ---
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AppWhite),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // EMAIL INPUT
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = MediumGray) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = BluePrimary
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PASSWORD INPUT
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = MediumGray) },
                    singleLine = true,
                    visualTransformation =
                        if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    trailingIcon = {
                        val iconText = if (passwordVisible) "Hide" else "Show"
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(
                                text = iconText,
                                color = BluePrimary,
                                fontSize = 13.sp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = BluePrimary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // LOGIN BUTTON
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BluePrimary
                    )
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppWhite
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FORGOT PASSWORD
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        text = "Forgot Password?",
                        color = BluePrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // SIGN UP LINK
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Don't have an account? ",
                color = MediumGray,
                fontSize = 15.sp
            )
            TextButton(onClick = onSignUpClick) {
                Text(
                    text = "Sign up",
                    color = BluePrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}