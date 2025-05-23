package com.example.minikeep.ui

import android.app.Application
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
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.material3.FloatingActionButton
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.minikeep.R
import androidx.compose.ui.draw.clip
import com.example.minikeep.viewmodel.DietPlanViewModel
import com.example.minikeep.viewmodel.UserViewModel
import com.example.minikeep.viewmodel.WorkoutPlanViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


/**
 * Main entry for the Profile screen.
 * Displays a greeting section, a banner image, workout suggestion area,
 * user data action buttons, and sign-out functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    drawerState: DrawerState,
    userViewModel: UserViewModel,
    dietPlanViewModel: DietPlanViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Redirect to login screen if user is not authenticated
    LaunchedEffect(Firebase.auth.currentUser) {
        if (userViewModel.loginUser.value == null && Firebase.auth.currentUser == null) {
            navController.navigate("login")
        }
    }

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
//            RecommendFitnessPlan()
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            NoActivitySection(navController = navController)
            Spacer(modifier = Modifier.height(12.dp))
            UserDataCard(
                navController = navController,
                showDialog = { showDialog = true },
                showPrivacy = { showPrivacyDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    userViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                },
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
            if (showPrivacyDialog) {
                PrivacyDialog(onDismiss = { showPrivacyDialog = false })
            }

        }
    }
}

/**
 * Displays a friendly greeting with the user's initial and a motivational message.
 */
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
                text = "Keep going! \uD83D\uDC5F",
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

/**
 * Section that encourages users to explore workout plans.
 * Includes a "Start Now" button to navigate to the Home screen.
 */
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
            fontSize = 22.sp,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
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

/**
 * Card containing user action shortcuts such as:
 * - Basic Info (navigate to form)
 * - Plan, Records, Calendar (TODO: define)
 * - Edit Profile (opens dialog)
 * - Privacy (opens policy dialog)
 */
@Composable
fun UserDataCard(
    navController: NavController,
    showDialog: () -> Unit,
    showPrivacy: () -> Unit
) {
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
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconWithLabel(Icons.Default.Edit, "Basic Info", onClick = { navController.navigate("form") })
//                IconWithLabel(Icons.Default.DateRange, "Plan", onClick = { /* TODO */ })
                IconWithLabel(Icons.Default.Place, "Map", onClick = {
                    navController.navigate("map")
                })
                IconWithLabel(Icons.Default.DateRange, "Calendar", onClick = {
                    navController.navigate("calendar")
                })
                IconWithLabel(Icons.Default.Settings, "Privacy", onClick = showPrivacy)
            }


//            Spacer(modifier = Modifier.height(16.dp))
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 24.dp),
//                horizontalArrangement = Arrangement.Start
//            ) {
//                IconWithLabel(Icons.Default.Edit, "Edit Profile", onClick = showDialog)
//                Spacer(modifier = Modifier.width(24.dp))
//                IconWithLabel(Icons.Default.Settings, "Privacy", onClick = showPrivacy)
//            }
        }
    }
}

/**
 * Dialog showing privacy policy information.
 * Explains how user data is stored and protected.
 */
@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Privacy Policy") },
        text = {
            Column(modifier = Modifier.heightIn(min = 100.dp, max = 300.dp)) {
                Text(
                    text = "🔐 We value your privacy.\n\n" +
                            "This app securely stores only the data necessary to help you track your fitness. " +
                            "Your personal details will never be shared with third parties.\n\n" +
                            "By using this app, you agree to our data policy. You can request data deletion anytime by contacting support.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

/**
 * Dialog that allows users to edit their username and password.
 * Includes form validation and confirmation logic.
 */
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


/**
 * Reusable component for a circular icon button with a text label underneath.
 * Used in user action shortcuts.
 */
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
    val userViewModel = UserViewModel(Application())
    val dietPlanViewModel = DietPlanViewModel(application = Application())
    val workoutPlanViewModel = WorkoutPlanViewModel(application = Application())
    ProfileScreen(
        navController,
        drawerState,
        userViewModel,
        dietPlanViewModel,
        workoutPlanViewModel
    )
}