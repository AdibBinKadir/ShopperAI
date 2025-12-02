package com.example.democse3310.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.democse3310.data.Budget
import com.example.democse3310.data.BudgetDao
import com.example.democse3310.data.BudgetItem
import com.example.democse3310.data.BudgetItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(
    private val budgetDao: BudgetDao,
    private val budgetItemDao: BudgetItemDao
) : ViewModel() {
    
    private val _budgetItems = MutableStateFlow<List<BudgetItem>>(emptyList())
    val budgetItems: StateFlow<List<BudgetItem>> = _budgetItems
    
    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent
    
    private val _monthlyBudget = MutableStateFlow(0.0)
    val monthlyBudget: StateFlow<Double> = _monthlyBudget
    
    private val _remainingBudget = MutableStateFlow(0.0)
    val remainingBudget: StateFlow<Double> = _remainingBudget
    
    fun loadBudgetData(userId: String) {
        val currentMonth = getCurrentMonth()
        viewModelScope.launch {
            budgetDao.getBudgetFlow(userId, currentMonth).collect { budget ->
                _monthlyBudget.value = budget?.monthlyBudget ?: 0.0
                updateRemainingBudget()
            }
        }
        viewModelScope.launch {
            budgetItemDao.getBudgetItemsFlow(userId).collect { items ->
                _budgetItems.value = items
                updateTotalAndRemaining()
            }
        }
        viewModelScope.launch {
            budgetItemDao.getTotalSpentFlow(userId).collect { total ->
                _totalSpent.value = total
                updateRemainingBudget()
            }
        }
    }
    
    fun setMonthlyBudget(userId: String, amount: Double) {
        viewModelScope.launch {
            val currentMonth = getCurrentMonth()
            val budget = Budget(userId, amount, currentMonth)
            budgetDao.insertBudget(budget)
        }
    }
    
    fun addBudgetItem(userId: String, itemName: String, cost: Double) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            val item = BudgetItem(
                userId = userId,
                itemName = itemName,
                cost = cost,
                dateAdded = currentDate
            )
            budgetItemDao.insertBudgetItem(item)
        }
    }
    
    fun removeBudgetItem(item: BudgetItem) {
        viewModelScope.launch {
            budgetItemDao.deleteBudgetItem(item)
        }
    }
    
    private fun updateTotalAndRemaining() {
        updateRemainingBudget()
    }
    
    private fun updateRemainingBudget() {
        val remaining = _monthlyBudget.value - _totalSpent.value
        _remainingBudget.value = if (remaining < 0) 0.0 else remaining
    }
    
    private fun getCurrentMonth(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

class BudgetViewModelFactory(
    private val budgetDao: BudgetDao,
    private val budgetItemDao: BudgetItemDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetDao, budgetItemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
