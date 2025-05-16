package com.example.minikeep.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import com.example.minikeep.ui.theme.backgroundLight
import com.example.minikeep.ui.theme.primaryLight
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Profile",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeSection(userName = "User")
            RecommendFitnessPlan()
            NoActivitySection(navController = navController)
            UserDataCard(navController = navController, showDialog = { showDialog = true })

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* TODO: sign out logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE6E6),
                    contentColor = Color(0xFFAA0000)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (showDialog) {
                EditProfileDialog(onDismiss = { showDialog = false })
            }
        }
    }
}

// Welcome area
@Composable
fun WelcomeSection(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userName.first().toString().uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Hi, $userName!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Let's stay healthy today!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun NoActivitySection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Explore your workout options!",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "Explore your workout options!",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            textAlign = TextAlign.Center
//        )
//        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { navController.navigate("home") },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color(0xFF00C27B)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF00C27B)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("Start Now", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun UserDataCard(navController: NavController, showDialog: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showBasicInfo by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Text("My Data", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(Icons.Default.Edit, "Basic Info", onClick = { navController.navigate("form") })
                IconWithLabel(Icons.Default.DateRange, "Plan", onClick = { /* TODO */ })
                IconWithLabel(Icons.Default.List, "Records", onClick = { /* TODO */ })
                IconWithLabel(Icons.Default.DateRange, "This Week", onClick = {
                    coroutineScope.launch {
                        Toast.makeText(context, "This Week: 3 workouts", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(Icons.Default.List, "Total", onClick = {
                    coroutineScope.launch {
                        Toast.makeText(context, "Total: 120 workouts", Toast.LENGTH_SHORT).show()
                    }
                })
                IconWithLabel(Icons.Default.DateRange, "Calendar", onClick = {
                    navController.navigate("calendar")
                })
                IconWithLabel(Icons.Default.Edit, "Edit Profile", onClick = showDialog)
                IconWithLabel(Icons.Default.Settings, "Settings", onClick = { /* TODO */ })
            }
        }
    }
}


@Composable
fun RecommendFitnessPlan() {
    val mockEvents = listOf(
        MockEvent("Back Day", "2025-04-09T10:00:00", "2025-04-09T11:00:00"),
        MockEvent("Chest Day", "2025-04-11T12:00:00", "2025-04-11T13:00:00")
    )

    LazyColumn(
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("My Workout Plan", style = MaterialTheme.typography.titleLarge)
        }
        items(mockEvents) { event ->
            EventCard(event)
        }
    }

}

@Composable
fun EditProfileDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("New Username") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun IconWithLabel(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Preview
@Composable
fun ProfileScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ProfileScreen(navController, drawerState)
}

