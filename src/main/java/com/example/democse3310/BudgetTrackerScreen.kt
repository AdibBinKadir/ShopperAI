package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.democse3310.data.ChatDatabase
import com.example.democse3310.viewmodel.BudgetViewModel
import com.example.democse3310.viewmodel.BudgetViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTrackerScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val database = ChatDatabase.getDatabase(context)
    val viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(
            database.budgetDao(),
            database.budgetItemDao()
        )
    )
    val userId = "default_user" // TODO: Get from actual user session
    
    LaunchedEffect(Unit) {
        viewModel.loadBudgetData(userId)
    }

    val budgetItems by viewModel.budgetItems.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val remainingBudget by viewModel.remainingBudget.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemCost by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Tracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Budget Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Budget",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${"%.2f".format(monthlyBudget)}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        TextButton(onClick = { showBudgetDialog = true }) {
                            Text("Edit")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Spent", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "$${"%.2f".format(totalSpent)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Column {
                            Text("Remaining", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "$${"%.2f".format(remainingBudget)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (remainingBudget >= 0) MaterialTheme.colorScheme.tertiary
                                       else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Budget Items List
            Text(
                text = "Items",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(budgetItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.itemName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.dateAdded,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$${"%.2f".format(item.cost)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = {
                                    viewModel.removeBudgetItem(item)
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
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

        // Add Item Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Item") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = itemCost,
                            onValueChange = { itemCost = it },
                            label = { Text("Cost") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cost = itemCost.toDoubleOrNull()
                            if (itemName.isNotBlank() && cost != null) {
                                viewModel.addBudgetItem(userId, itemName, cost)
                                itemName = ""
                                itemCost = ""
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Set Budget Dialog
        if (showBudgetDialog) {
            AlertDialog(
                onDismissRequest = { showBudgetDialog = false },
                title = { Text("Set Budget") },
                text = {
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Budget Amount") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val budget = budgetInput.toDoubleOrNull()
                            if (budget != null && budget > 0) {
                                viewModel.setMonthlyBudget(userId, budget)
                                budgetInput = ""
                                showBudgetDialog = false
                            }
                        }
                    ) {
                        Text("Set")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBudgetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

