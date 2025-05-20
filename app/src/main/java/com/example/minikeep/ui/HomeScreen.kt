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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.foundation.clickable
import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.collectAsState

data class ExerciseData(
    val sets: Int = 0,
    val weightOrTime: String = "",
    val progress: Float = 0f
)

@Composable
fun TodayWorkoutPlanSection() {
    val categories = listOf("Strength", "Cardio", "Flexibility")
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val exerciseOptions = mapOf(
        "Strength" to listOf("Bench Press", "Squat", "Deadlift", "Shoulder Press", "Bicep Curl", "Leg Press"),
        "Cardio" to listOf("Running", "Cycling", "Treadmill", "Jump Rope", "Rowing Machine"),
        "Flexibility" to listOf("Yoga", "Stretching", "Pilates", "Barre")
    )

    val selectedExercises = remember { mutableStateMapOf<String, ExerciseData>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text("Today Workout Plan", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        TabRow(selectedTabIndex = selectedTab) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val currentCategory = categories[selectedTab]
        val currentOptions = exerciseOptions[currentCategory] ?: emptyList()

        currentOptions.forEach { exercise ->
            val isSelected = selectedExercises.containsKey(exercise)
            val data = selectedExercises[exercise] ?: ExerciseData()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) {
                            selectedExercises.remove(exercise)
                        } else {
                            selectedExercises[exercise] = ExerciseData()
                        }
                    }
                    .padding(vertical = 6.dp)
                    .background(
                        if (data.progress >= 1f) Color(0xFFE8F5E9) else Color.Transparent,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            if (it) selectedExercises[exercise] = ExerciseData()
                            else selectedExercises.remove(exercise)
                        }
                    )
                    Text(
                        text = if (data.progress >= 1f) "$exercise ✅" else exercise,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isSelected) {
                        TextButton(onClick = {
                            selectedExercises[exercise] = ExerciseData()
                        }) {
                            Text("Reset")
                        }
                    }
                }

                if (isSelected) {
                    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                        Row {
                            OutlinedTextField(
                                value = data.sets.toString(),
                                onValueChange = {
                                    val sets = it.toIntOrNull() ?: 0
                                    selectedExercises[exercise] = data.copy(sets = sets)
                                },
                                label = { Text("Sets") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = data.weightOrTime,
                                onValueChange = {
                                    selectedExercises[exercise] = data.copy(weightOrTime = it)
                                },
                                label = { Text("Weight / Time") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Progress: ${(data.progress * 100).toInt()}%")
                        Slider(
                            value = data.progress,
                            onValueChange = {
                                selectedExercises[exercise] = data.copy(progress = it)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (selectedExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                Toast.makeText(context, "Template Saved!", Toast.LENGTH_SHORT).show()
            }) {
                Text("Save as Template")
            }
        }
    }
}

@Composable
fun TodayDietPlanSection() {
    val context = LocalContext.current

    val meals = listOf("Breakfast", "Lunch", "Dinner")
    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }
    val mealInputs = remember { mutableStateMapOf<String, String>() }
    val isEditing = remember { mutableStateMapOf<String, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Today Diet Plan",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        meals.forEach { meal ->
            val isChecked = checkedStates[meal] ?: false
            val inputText = mealInputs[meal] ?: ""
            val editing = isEditing[meal] ?: true

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                checkedStates[meal] = it
                                if (it) {
                                    Toast.makeText(context, "$meal completed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        Text(
                            text = if (isChecked) "$meal ✅" else meal,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            // Reset everything
                            checkedStates[meal] = false
                            mealInputs[meal] = ""
                            isEditing[meal] = true
                        }) {
                            Text("Reset")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (editing) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { mealInputs[meal] = it },
                            label = { Text("What do you want to eat?") },
                            placeholder = { Text("e.g. Toast + Eggs + Milk") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            isEditing[meal] = false
                        }) {
                            Text("Save")
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = inputText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = {
                                isEditing[meal] = true
                            }) {
                                Text("Edit")
                            }
                        }
                    }
                }
            }
        }
    }
}

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

    val currentUserId = userViewModel.loginUser.collectAsState().value?.id

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            userDetailViewModel.getUserDetailByUserId(currentUserId) { detail ->
                userDetailState = detail
            }
        }
    }

    LaunchedEffect(userViewModel.loginUser) {
        if (userViewModel.loginUser.value == null && Firebase.auth.currentUser == null) {
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
        modifier = Modifier.systemBarsPadding()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            GreetingSection()
            TodayWorkoutPlanSection()
            TodayDietPlanSection()
            userDetailState?.let {
                FormResultCard(it)
            }
        }
    }
}


@Composable
fun GreetingSection() {
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
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
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