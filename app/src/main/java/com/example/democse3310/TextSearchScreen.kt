package com.example.democse3310

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.democse3310.ui.theme.Spacing
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.democse3310.viewmodel.TextSearchViewModel

@Composable
fun TextSearchScreen(navController: NavController, vm: TextSearchViewModel = viewModel()) {
    Column(modifier = Modifier.fillMaxSize().padding(Spacing.md)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Text Search", style = MaterialTheme.typography.headlineMedium)
                Text("Search by product name or description", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))
            }
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        OutlinedTextField(
            value = vm.query.value,
            onValueChange = { vm.query.value = it },
            label = { Text("Search for a product") },
            placeholder = { Text("e.g., wireless headphones, laptop, running shoes") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { vm.searchProducts() }, modifier = Modifier.height(44.dp), shape = MaterialTheme.shapes.small) {
                Text("Search", style = MaterialTheme.typography.titleMedium)
            }

            TextButton(onClick = {
                vm.query.value = ""
                vm.products.clear()
            }) {
                Text("Clear", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            "Search will use web-scraping (BeautifulSoup) per SRA section 2.2",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        if (vm.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (vm.products.isEmpty() && vm.query.value.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No results found. Try a different search term.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.weight(1f)
            ) {
                items(vm.products) { product ->
                    ProductListItem(product)
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: com.example.democse3310.data.Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                product.description, 
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Vendor: ${product.vendor}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "\$${product.price}", 
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(onClick = { 
                    // TODO: Navigate to product URL
                }) {
                    Text("View")
                }
            }
        }
    }
}