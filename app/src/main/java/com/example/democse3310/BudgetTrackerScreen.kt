package com.example.democse3310

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.democse3310.data.ChatDatabase
import com.example.democse3310.data.getCurrentUserIdFlow
import com.example.democse3310.ui.theme.Spacing
import com.example.democse3310.viewmodel.BudgetViewModel
import com.example.democse3310.viewmodel.BudgetViewModelFactory

@Composable
fun BudgetTrackerScreen(navController: NavController) {
    val context = LocalContext.current
    val database = remember { ChatDatabase.getDatabase(context) }
    val currentUserId by getCurrentUserIdFlow(context).collectAsState(initial = "")
    
    val viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(
            database.budgetDao(),
            database.budgetItemDao()
        )
    )
    
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadBudgetData(currentUserId)
        }
    }
    
    val budgetItems by viewModel.budgetItems.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val remainingBudget by viewModel.remainingBudget.collectAsState()
    
    var budgetInputValue by remember { mutableStateOf("") }
    var itemNameInput by remember { mutableStateOf("") }
    var itemCostInput by remember { mutableStateOf("") }
    var showBudgetDialog by remember { mutableStateOf(monthlyBudget == 0.0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Text("Budget Tracker", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(Spacing.md))
        
        // Budget Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Monthly Budget", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "$%.2f".format(monthlyBudget),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                    Button(
                        onClick = { showBudgetDialog = true },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Set Budget")
                    }
                }
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                // Progress bar
                LinearProgressIndicator(
                    progress = (if (monthlyBudget > 0) (totalSpent / monthlyBudget).toFloat() else 0f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surface,
                    color = if (remainingBudget > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Spent", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "$%.2f".format(totalSpent),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Remaining", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "$%.2f".format(remainingBudget),
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (remainingBudget > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
        
        // Add Item Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.md),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md)
            ) {
                Text("Add Purchase", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                OutlinedTextField(
                    value = itemNameInput,
                    onValueChange = { itemNameInput = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                OutlinedTextField(
                    value = itemCostInput,
                    onValueChange = { newValue: String ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            itemCostInput = newValue
                        }
                    },
                    label = { Text("Cost ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(Spacing.sm))
                
                Button(
                    onClick = {
                        if (itemNameInput.isNotEmpty() && itemCostInput.isNotEmpty()) {
                            val cost = itemCostInput.toDoubleOrNull() ?: 0.0
                            viewModel.addBudgetItem(currentUserId, itemNameInput, cost)
                            itemNameInput = ""
                            itemCostInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Add Item")
                }
            }
        }
        
        // Items List
        Text("Purchases", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(Spacing.sm))
        
        if (budgetItems.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    "No purchases yet",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(Spacing.lg)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                items(budgetItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacing.md),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.itemName, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    item.dateAdded,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "$%.2f".format(item.cost),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                IconButton(
                                    onClick = { viewModel.removeBudgetItem(item) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.lg))
        
        // Back Button
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Back to Home")
        }
    }
    
    // Budget Dialog
    if (showBudgetDialog) {
        BudgetDialog(
            value = budgetInputValue,
            onValueChange = { budgetInputValue = it },
            onConfirm = {
                val amount = budgetInputValue.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    viewModel.setMonthlyBudget(currentUserId, amount)
                    budgetInputValue = ""
                    showBudgetDialog = false
                }
            },
            onDismiss = {
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun BudgetDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(Spacing.lg),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Set Monthly Budget", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(Spacing.md))
                
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue: String ->
                        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                            onValueChange(newValue)
                        }
                    },
                    label = { Text("Budget Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                )
                
                Spacer(modifier = Modifier.height(Spacing.md))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Set")
                    }
                }
            }
        }
    }
}
