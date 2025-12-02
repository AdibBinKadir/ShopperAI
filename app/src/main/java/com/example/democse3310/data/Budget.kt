package com.example.democse3310.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val userId: String,
    val monthlyBudget: Double,
    val month: String // Format: "YYYY-MM" for per-month tracking
)

@Entity(tableName = "budget_items")
data class BudgetItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val itemName: String,
    val cost: Double,
    val dateAdded: String // Format: "YYYY-MM-DD HH:mm:ss"
)
