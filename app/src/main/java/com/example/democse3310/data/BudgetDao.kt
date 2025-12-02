package com.example.democse3310.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month")
    fun getBudgetFlow(userId: String, month: String): Flow<Budget?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month")
    suspend fun getBudget(userId: String, month: String): Budget?
}

@Dao
interface BudgetItemDao {
    @Query("SELECT * FROM budget_items WHERE userId = :userId ORDER BY dateAdded DESC")
    fun getBudgetItemsFlow(userId: String): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budget_items WHERE userId = :userId ORDER BY dateAdded DESC")
    suspend fun getBudgetItems(userId: String): List<BudgetItem>

    @Insert
    suspend fun insertBudgetItem(item: BudgetItem)

    @Delete
    suspend fun deleteBudgetItem(item: BudgetItem)

    @Query("SELECT COALESCE(SUM(cost), 0.0) FROM budget_items WHERE userId = :userId")
    fun getTotalSpentFlow(userId: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(cost), 0.0) FROM budget_items WHERE userId = :userId")
    suspend fun getTotalSpent(userId: String): Double
}
