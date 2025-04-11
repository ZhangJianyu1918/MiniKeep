package com.example.minikeep.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.Manifest
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.android.gms.maps.CameraUpdateFactory

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
//    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 位置状态
//    var userLocation by remember { mutableStateOf<LatLng?>(null) }



    // 位置权限
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // 获取用户位置
//    LaunchedEffect(locationPermissionState.status.isGranted) {
//        if (locationPermissionState.status.isGranted) {
//            try {
//                locationClient.lastLocation.addOnSuccessListener { location ->
//                    location?.let {
//                        userLocation = LatLng(it.latitude, it.longitude)
//                    }
//                }
//            } catch (e: SecurityException) {
//                // 处理权限异常
//            }
//        }
//    }
    // 初始化 CameraPositionState
    val defaultLocation = LatLng(-37.8136, -144.9631) // 替换为你的城市坐标
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    // 监听 userLocation，自动调整地图视角
//    LaunchedEffect(userLocation) {
//        if (userLocation != null) {
//            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f)
//            cameraPositionState.animate(cameraUpdate, durationMs = 1000)
//        } else {
//            // 用户位置为空时，手动设置到墨尔本
//            val melbourneUpdate = CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f)
//            cameraPositionState.animate(melbourneUpdate, durationMs = 1000)
//        }
//    }

    // 默认位置（例如某个健身公园）


    // 健身相关标记（例如健身房、公园）
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
                "Map",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding(),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 显示地图
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
//                    myLocationButtonEnabled = locationPermissionState.status.isGranted
                    myLocationButtonEnabled = false
                ),
                properties = MapProperties(
//                    isMyLocationEnabled = locationPermissionState.status.isGranted
                    isMyLocationEnabled = false
                )
            ) {
                // 添加健身地点标记
                fitnessLocations.forEach { location ->
                    Marker(
                        state = MarkerState(position = location.position),
                        title = location.name,
                        snippet = location.description,
                        onClick = {
                            // 可选：点击标记跳转到健身详情页面
                            navController.navigate("form") // 替换为实际路由
                            true
                        }
                    )
                }
            }

            // 权限提示
            if (!locationPermissionState.status.isGranted) {
                Button(
                    onClick = { locationPermissionState.launchPermissionRequest() },
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {
                    Text("Enable Location Permission")
                }
            }
        }
    }
}

// 数据类用于存储健身地点信息
data class FitnessLocation(
    val name: String,
    val position: LatLng,
    val description: String
)