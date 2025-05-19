package com.example.minikeep.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.viewmodel.UserViewModel
import androidx.compose.material3.rememberDrawerState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    drawerState: DrawerState,
    userViewModel: UserViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState  = remember { SnackbarHostState() }
    val loginUser by userViewModel.loginUser.collectAsState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(loginUser) {
        if (loginUser != null || Firebase.auth.currentUser != null) {
            navController.navigate("home")
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Register",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.systemBarsPadding(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Register", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        isError = email.isNotEmpty() && !userViewModel.isValidEmail(email)
                    )
                    if (email.isNotEmpty() && !userViewModel.isValidEmail(email)) {
                        Text(
                            text = "Email format is wrong",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val iconText = if (passwordVisible) "Hide" else "Show"
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(iconText)
                            }
                        },
                        isError = password.isNotEmpty() && !userViewModel.isValidPassword(password)
                    )
                    if (password.isNotEmpty() && !userViewModel.isValidPassword(password)) {
                        Text(
                            text = "Password length should be over 6.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (!userViewModel.isValidEmail(email) || !userViewModel.isValidPassword(password)) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Your Input is invalid!")
                                }
                                return@Button
                            }
                            userViewModel.insertUser(
                            User(email = email, password = password)
                        )},
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Register")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button (
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val userViewModel = UserViewModel(Application())
    RegisterScreen(navController, drawerState, userViewModel)
}