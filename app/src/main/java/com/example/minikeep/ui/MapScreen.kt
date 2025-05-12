@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.example.minikeep.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val monashClayton = LatLng(-37.9150, 145.1347) // Default destination
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(monashClayton, 14f)
    }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var inputAddress by remember { mutableStateOf("") }

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
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
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            // Input address
            OutlinedTextField(
                value = inputAddress,
                onValueChange = { inputAddress = it },
                label = { Text("Enter destination address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Navigate to typed destination
            Button(
                onClick = {
                    if (inputAddress.isNotBlank()) {
                        launchNavigation(context, inputAddress)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("Navigate to Entered Address")
            }

            // Navigate to Monash Clayton
            Button(
                onClick = {
                    launchNavigation(context, "${monashClayton.latitude},${monashClayton.longitude}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Navigate to Monash Clayton")
            }

            // Navigate from current location (if available)
            Button(
                onClick = {
                    currentLocation?.let {
                        launchNavigation(
                            context,
                            "${monashClayton.latitude},${monashClayton.longitude}",
                            origin = "${it.latitude},${it.longitude}"
                        )
                    }
                },
                enabled = currentLocation != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Navigate from My Location to Monash Clayton")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Map display
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
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
                    snippet = "Default destination"
                )
            }
        }
    }
}

fun launchNavigation(context: Context, destination: String, origin: String? = null) {
    val uriString = if (origin != null) {
        "https://www.google.com/maps/dir/?api=1&origin=${Uri.encode(origin)}&destination=${Uri.encode(destination)}&travelmode=driving"
    } else {
        "google.navigation:q=${Uri.encode(destination)}"
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString)).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}
