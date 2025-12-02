package com.example.democse3310

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

data class StoreLocation(
    val name: String,
    val type: String, // "Target", "Walmart", "Best Buy"
    val position: LatLng,
    val address: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }
    
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedStore by remember { mutableStateOf<StoreLocation?>(null) }
    
    // Default camera position (Dallas, TX area)
    val defaultPosition = LatLng(32.7767, -96.7970)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: defaultPosition, 12f)
    }
    
    // Sample store locations (you'll replace these with actual API calls)
    val stores = remember {
        listOf(
            StoreLocation("Target", "Target", LatLng(32.7867, -96.8070), "123 Main St, Dallas, TX"),
            StoreLocation("Walmart Supercenter", "Walmart", LatLng(32.7667, -96.7870), "456 Oak Ave, Dallas, TX"),
            StoreLocation("Best Buy", "Best Buy", LatLng(32.7967, -96.7770), "789 Elm St, Dallas, TX"),
            StoreLocation("Target Express", "Target", LatLng(32.7567, -96.8170), "321 Pine Rd, Dallas, TX"),
            StoreLocation("Walmart Neighborhood Market", "Walmart", LatLng(32.8067, -96.7670), "654 Cedar Ln, Dallas, TX"),
        )
    }
    
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Stores") },
                actions = {
                    if (!hasLocationPermission) {
                        TextButton(onClick = {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }) {
                            Text("Enable Location")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = hasLocationPermission
                )
            ) {
                // Add markers for each store
                stores.forEach { store ->
                    val icon = when (store.type) {
                        "Target" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                        )
                        "Walmart" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
                        )
                        "Best Buy" -> com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW
                        )
                        else -> com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker()
                    }
                    
                    Marker(
                        state = MarkerState(position = store.position),
                        title = store.name,
                        snippet = store.address,
                        icon = icon,
                        onClick = {
                            selectedStore = store
                            true
                        }
                    )
                }
            }
            
            // Store info card
            selectedStore?.let { store ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = store.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = store.type,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = store.address,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { /* Open directions */ },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Directions")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { selectedStore = null },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Close")
                            }
                        }
                    }
                }
            }
            
            // Legend card
            if (selectedStore == null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Store Types",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LegendItem("🔴 Target", MaterialTheme.colorScheme.error)
                        LegendItem("🔵 Walmart", MaterialTheme.colorScheme.primary)
                        LegendItem("🟡 Best Buy", MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
