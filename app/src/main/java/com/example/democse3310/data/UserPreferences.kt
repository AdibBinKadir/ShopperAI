package com.example.democse3310.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey val userId: String,
    val email: String,
    val assistantWelcomeShown: Boolean = false
)
