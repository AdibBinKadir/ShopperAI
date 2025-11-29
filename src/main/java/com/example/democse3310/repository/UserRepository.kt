package com.example.democse3310.repository

import com.example.democse3310.data.User

object UserRepository {
    private val users = mutableListOf<User>(
        // Default test user - remove in production
        User(
            fullName = "Test User",
            email = "test@test.com",
            phoneNumber = "1234567890",
            userId = "testuser",
            password = "Test1234"
        )
    )
    
    fun registerUser(user: User): Boolean {
        // Check if user ID or email already exists
        if (users.any { it.userId == user.userId || it.email == user.email }) {
            return false
        }
        users.add(user)
        return true
    }
    
    fun validateCredentials(userIdOrEmail: String, password: String): Boolean {
        return users.any { 
            (it.userId == userIdOrEmail || it.email == userIdOrEmail) && it.password == password
        }
    }
    
    fun userExists(userId: String, email: String): Boolean {
        return users.any { it.userId == userId || it.email == email }
    }
}
