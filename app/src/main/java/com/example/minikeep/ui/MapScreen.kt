package com.example.minikeep.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    // Set the default location to Melbourne
    val melbourneLocation = LatLng(-37.8136, 144.9631) // Melbourne coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(melbourneLocation, 14f) // Fixed zoom level for Melbourne
    }

    // Predefined fitness locations in Melbourne
    val fitnessLocations = listOf(
        FitnessLocation("Fitness First Melbourne Central", LatLng(-37.8116, 144.9625), "Fully equipped city centre gym"),
        FitnessLocation("F45 Training Southbank", LatLng(-37.8230, 144.9587), "High-intensity team training courses"),
        FitnessLocation("Flagstaff Gardens", LatLng(-37.8105, 144.9546), "City parks for running and outdoor exercise"),
        FitnessLocation("Royal Botanic Gardens", LatLng(-37.8315, 144.9796), "Beautiful scenery, suitable for walking and jogging"),
        FitnessLocation("Princes Park", LatLng(-37.7834, 144.9582), "Spacious green space and fitness trails")
    )

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                title = "Map",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Display the map centered on Melbourne
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false // Disable location button
                ),
                properties = MapProperties(
                    isMyLocationEnabled = false // Disable user location
                )
            ) {
                // Add markers for fitness locations in Melbourne
                fitnessLocations.forEach { location ->
                    Marker(
                        state = MarkerState(position = location.position),
                        title = location.name,
                        snippet = location.description,
                        onClick = {
                            // Navigate to a form screen when a marker is clicked
                            navController.navigate("form")
                            true
                        }
                    )
                }
            }
        }
    }
}

// Data class for fitness locations
data class FitnessLocation(
    val name: String,
    val position: LatLng,
    val description: String
)