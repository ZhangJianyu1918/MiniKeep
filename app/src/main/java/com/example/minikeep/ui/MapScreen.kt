package com.example.minikeep.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val monashClayton = LatLng(-37.9150, 145.1347)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(monashClayton, 14f)
    }

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var hasCheckedInToday by remember { mutableStateOf(false) }
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    var lastCheckInDate by remember { mutableStateOf("") }

    var selectedPoint by remember { mutableStateOf<LatLng?>(null) }
    var distanceText by remember { mutableStateOf("") }
    var isInsideGeofence by remember { mutableStateOf(false) }

    fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                title = "Map & Navigation",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (lastCheckInDate == today) "✅ Checked in: $today" else "Not checked in yet",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Button(
                onClick = {
                    lastCheckInDate = today
                    hasCheckedInToday = true
                },
                enabled = lastCheckInDate != today && isInsideGeofence,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Check in @ Monash Clayton")
            }

            if (distanceText.isNotEmpty()) {
                Text(
                    text = distanceText,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    selectedPoint = latLng
                    val distance = haversineDistance(
                        latLng.latitude, latLng.longitude,
                        monashClayton.latitude, monashClayton.longitude
                    )
                    distanceText = String.format("Distance to Monash Clayton: %.2f meters", distance)
                    isInsideGeofence = distance <= 100
                },
                properties = MapProperties(
                    isMyLocationEnabled = permissionState.allPermissionsGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabled = true
                )
            ) {
                Marker(
                    state = MarkerState(position = monashClayton),
                    title = "Monash University Clayton",
                    snippet = "Default Fitness Location"
                )
                Circle(
                    center = monashClayton,
                    radius = 100.0,
                    strokeColor = MaterialTheme.colorScheme.primary,
                    fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                selectedPoint?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Selected Point"
                    )
                    Circle(
                        center = it,
                        radius = 3.0,
                        fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
