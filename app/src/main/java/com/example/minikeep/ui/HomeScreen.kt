package com.example.minikeep.ui

import android.annotation.SuppressLint
import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.minikeep.data.local.entity.UserDetail
import com.example.minikeep.viewmodel.UserDetailViewModel
import com.example.minikeep.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    drawerState: DrawerState,
    userViewModel: UserViewModel,
    userDetailViewModel: UserDetailViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser = Firebase.auth.currentUser
    val userName = currentUser?.displayName ?: currentUser?.email ?: "User"
    var userDetailState by remember { mutableStateOf<UserDetail?>(null) }

    LaunchedEffect(currentUser?.email) {
        if (currentUser != null) {
            val result = userDetailViewModel.queryUserDetailFromCloudDatabase()
            userDetailState = result
        }
    }

    LaunchedEffect(userViewModel.loginUser) {
        if (userViewModel.loginUser == null) {
            navController.navigate("login")
        }
    }
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("login")
        }
    }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Home",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding() // 支持 Edge-to-Edge
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GreetingSection(userName = userName)
            CheckBoxList("Today Workout Plan")
            CheckBoxList("Today Diet Plan")

            FormResultCard(userDetailState)

        }
    }
}
@Composable
fun GreetingSection(userName: String = "User") {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good Morning ☀️"
        in 12..17 -> "Good Afternoon 🌤️"
        in 18..21 -> "Good Evening 🌙"
        else -> "Good Night 🌌"
    }

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CheckBoxList(title: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp).background(color = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        var checked1 by remember { mutableStateOf(false) }
        var checked2 by remember { mutableStateOf(false) }
        Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
        CheckoutBox(false, onCheckedChange = { checked1 = it },"Toast Text1", "Title1", "Description1")
        CheckoutBox(false, onCheckedChange = { checked2 = it },"Toast Text2", "Title2", "Description2")
    }
}


@Composable
fun CheckoutBox(checked: Boolean, onCheckedChange: (Boolean) -> Unit,toastText: String, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val context = LocalContext.current
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
                if (isChecked) {
                    Toast.makeText(
                        context,
                        toastText,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniKeepTopBar(
    title: String,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = {
                coroutineScope.launch { drawerState.open() }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Open Drawer")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}


@Preview
@Composable
fun HomeScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val application = Application()
    val userViewModel = UserViewModel(application)
    val userDetailViewModel = UserDetailViewModel(application)
    HomeScreen(navController, drawerState, userViewModel, userDetailViewModel)
}