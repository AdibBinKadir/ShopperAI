package com.example.democse3310.data

data class User(
    val userId: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val securityQuestion: String,
    val securityAnswer: String
)