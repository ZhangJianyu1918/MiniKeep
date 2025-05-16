package com.example.minikeep.ui

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.minikeep.R
import com.example.minikeep.ui.theme.onPrimaryLight
import com.example.minikeep.ui.theme.primaryLight
import com.example.minikeep.ui.theme.secondaryDark
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.launch

data class WorkoutTask(
    val title: String,
    val description: String,
    val progress: Float = 0.0f, // 0.0 ~ 1.0
    val isCompleted: Boolean = false
)

data class ExerciseData(
    val sets: Int = 0,
    val weightOrTime: String = "",
    val progress: Float = 0f
)

@Composable
fun ExerciseCard(
    name: String,
    data: ExerciseData,
    onReset: () -> Unit,
    onUpdate: (ExerciseData) -> Unit
) {
    val isCompleted = data.progress >= 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "$name ${if (isCompleted) "✅" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onReset) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 设置组数和时间/重量
            Row {
                OutlinedTextField(
                    value = data.sets.toString(),
                    onValueChange = {
                        onUpdate(data.copy(sets = it.toIntOrNull() ?: 0))
                    },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = data.weightOrTime,
                    onValueChange = {
                        onUpdate(data.copy(weightOrTime = it))
                    },
                    label = { Text("Time/Weight") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 进度条
            Slider(
                value = data.progress,
                onValueChange = {
                    onUpdate(data.copy(progress = it))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun WorkoutItemCard(task: WorkoutTask, onCheckedChange: (Boolean) -> Unit) {
    var checked by remember { mutableStateOf(task.isCompleted) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        onCheckedChange(it)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(task.title, style = MaterialTheme.typography.titleMedium)
                    Text(task.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = task.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

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
fun DropdownMenuMultiSelect(
    options: List<String>,
    selectedOptions: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Choose Workouts (${selectedOptions.size})")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        val updated = if (label in selectedOptions) {
                            selectedOptions - label
                        } else {
                            selectedOptions + label
                        }
                        onSelectionChange(updated)
                    },
                    trailingIcon = {
                        if (label in selectedOptions) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red)
                        }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    val currentUser = Firebase.auth.currentUser
    val userName = currentUser?.displayName ?: currentUser?.email ?: "User"

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
                .padding(padding)
        ) {
            GreetingSection(userName = userName)
            TodayWorkoutPlanSection()
            CheckBoxList("Today Diet Plan")

            FormResultCard()

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
    HomeScreen(navController, drawerState)
}