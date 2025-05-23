package com.example.minikeep.ui

import android.annotation.SuppressLint
import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.material3.TabRow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Tab
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import com.example.minikeep.R
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.collectAsState
import com.example.minikeep.data.local.entity.DietPlan
import com.example.minikeep.data.local.entity.WorkoutPlan
import com.example.minikeep.viewmodel.DietPlanViewModel
import com.example.minikeep.viewmodel.WorkoutPlanViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await


data class ExerciseData(
    val sets: Int = 0,
    val weightOrTime: String = "",
    val progress: Float = 0f
)

/**
 * Section for users to manage today's workout plan.
 * Allows users to select exercises, input sets and completed sets,
 * view progress, and save as a workout template.
 */
@Composable
fun TodayWorkoutPlanSection(
    userViewModel: UserViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel
) {
    val context = LocalContext.current
    val categories = listOf("Strength", "Cardio", "Flexibility")
    var selectedTab by remember { mutableStateOf(0) }

    val initialOptions = mapOf(
        "Strength" to listOf("Bench Press", "Squat", "Deadlift"),
        "Cardio" to listOf("Running", "Cycling", "Jump Rope"),
        "Flexibility" to listOf("Yoga", "Stretching", "Pilates")
    )
    val exerciseOptions =
        remember { mutableStateMapOf<String, List<String>>().apply { putAll(initialOptions) } }
    val newExerciseInput = remember { mutableStateMapOf<String, String>() }

    val selectedExercises = remember { mutableStateMapOf<String, ExerciseData>() }
    val setsTextMap = remember { mutableStateMapOf<String, String>() }
    val completedTextMap = remember { mutableStateMapOf<String, String>() }

    val currentUser by userViewModel.loginUser.collectAsState()
    val googleUser = Firebase.auth.currentUser
    val firestore = FirebaseFirestore.getInstance()

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
        val contentList = ArrayList<String>()
        val targetSetsList = ArrayList<Int>()
        val completedSetsList = ArrayList<Int>()
        val progressList = ArrayList<Float>()
        currentOptions.forEach { exercise ->
            val isSelected = selectedExercises.containsKey(exercise)
            val data = selectedExercises[exercise] ?: ExerciseData()
            val setsText = setsTextMap[exercise] ?: ""
            val completedText = completedTextMap[exercise] ?: ""

            val sets = setsText.toIntOrNull() ?: 0
            val completed = completedText.toIntOrNull() ?: 0
            val progress = if (sets > 0) (completed.toFloat() / sets).coerceIn(0f, 1f) else 0f

            targetSetsList.add(sets)
            completedSetsList.add(completed)
            progressList.add(progress)
            contentList.add(exercise)

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
                        if (progress >= 1f) Color(0xFFE8F5E9) else Color.Transparent,
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
                        text = if (progress >= 1f) "$exercise ✅" else exercise,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isSelected) {
                        TextButton(onClick = {
                            selectedExercises[exercise] = ExerciseData()
                            setsTextMap[exercise] = ""
                            completedTextMap[exercise] = ""
                        }) {
                            Text("Reset")
                        }
                    }
                }

                if (isSelected) {
                    Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                        Row {
                            OutlinedTextField(
                                value = setsText,
                                onValueChange = {
                                    setsTextMap[exercise] = it.filter { ch -> ch.isDigit() }
                                    val newSets = it.toIntOrNull() ?: 0
                                    val currentCompleted =
                                        completedTextMap[exercise]?.toIntOrNull() ?: 0
                                    val newProgress =
                                        if (newSets > 0) (currentCompleted.toFloat() / newSets).coerceIn(
                                            0f,
                                            1f
                                        ) else 0f
                                    selectedExercises[exercise] =
                                        data.copy(sets = newSets, progress = newProgress)
                                },
                                label = { Text("Sets") },
                                isError = setsText.isEmpty(),
                                supportingText = {
                                    if (setsText.isEmpty()) Text(
                                        "Required",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = completedText,
                                onValueChange = {
                                    completedTextMap[exercise] = it.filter { ch -> ch.isDigit() }
                                    val currentSets = setsTextMap[exercise]?.toIntOrNull() ?: 0
                                    val newCompleted = it.toIntOrNull() ?: 0
                                    val newProgress =
                                        if (currentSets > 0) (newCompleted.toFloat() / currentSets).coerceIn(
                                            0f,
                                            1f
                                        ) else 0f
                                    selectedExercises[exercise] = data.copy(progress = newProgress)
                                },
                                label = { Text("Completed") },
                                enabled = sets > 0,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Progress: ${(progress * 100).toInt()}%")
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = newExerciseInput[currentCategory] ?: "",
            onValueChange = { newExerciseInput[currentCategory] = it },
            label = { Text("Add new exercise") },
            placeholder = { Text("e.g. Incline Dumbbell Press") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (selectedExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val newExercise = newExerciseInput[currentCategory]?.trim().orEmpty()
                        if (newExercise.isNotBlank()) {
                            val updatedList =
                                (exerciseOptions[currentCategory] ?: emptyList()) + newExercise
                            exerciseOptions[currentCategory] = updatedList
                            newExerciseInput[currentCategory] = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add")
                }

                if (selectedExercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        if (googleUser != null) {
                            val newWorkoutPlan = mapOf(
                                "email" to googleUser.email,
                                "content" to contentList,
                                "targetSets" to targetSetsList,
                                "completedSets" to completedSetsList,
                                "process" to progressList
                            )
                            firestore.collection("workout").document(googleUser.email.toString())
                                .set(
                                    newWorkoutPlan
                                )
                        } else if (currentUser != null) {
                            for (i in targetSetsList.indices) {
                                val localWorkoutPlan = WorkoutPlan(
                                    userId = currentUser!!.id,
                                    content = contentList[i],
                                    targetSets = targetSetsList[i],
                                    completedSets = completedSetsList[i]
                                )
                                workoutPlanViewModel.addWorkoutPlan(localWorkoutPlan)
                            }
                        }
                        Toast.makeText(context, "Template Saved!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save as Template")
                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Template Saved!", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save as Template")
                        }
                    }
                }
            }
        }
    }
}
/**
 * Section for users to manage today's diet plan.
 * Includes editable entries for breakfast, lunch, and dinner with check-off status.
 */
@Composable
fun TodayDietPlanSection(userViewModel: UserViewModel, dietPlanViewModel: DietPlanViewModel) {
    val context = LocalContext.current

    val meals = listOf("Breakfast", "Lunch", "Dinner")
    val checkedStates = remember { mutableStateMapOf<String, Boolean>() }
    val mealInputs = remember { mutableStateMapOf<String, String>() }
    val isEditing = remember { mutableStateMapOf<String, Boolean>() }

    val currentUser by userViewModel.loginUser.collectAsState()
    val googleUser = Firebase.auth.currentUser
    val firestore = FirebaseFirestore.getInstance()

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
                            enabled = inputText.isNotBlank() && !editing,
                            onCheckedChange = {
                                if (inputText.isNotBlank() && !editing) {
                                    checkedStates[meal] = it
                                    if (googleUser != null) {
                                        val dietPlan = mapOf(
                                            "email" to googleUser.email,
                                            "type" to meal,
                                            "food" to inputText,
                                            "isCompleted" to it
                                        )
                                        firestore.collection("diet").document(googleUser.email.toString()).set(
                                            dietPlan
                                        )
                                    } else if (currentUser != null) {
                                        val type: Int = when (meal) {
                                            "Breakfast" -> {
                                                0
                                            }
                                            "Lunch" -> {
                                                1
                                            }
                                            else -> {
                                                2
                                            }
                                        }
                                        val localDietPlan = DietPlan(
                                            userId = currentUser!!.id,
                                            food = inputText,
                                            mealType = type,
                                            isCompleted = it
                                        )
                                        dietPlanViewModel.updateDietPlan(localDietPlan)
                                    }
                                    if (it) {
                                        Toast.makeText(context, "$meal completed!", Toast.LENGTH_SHORT).show()
                                    }
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
                        val placeholderText = when (meal) {
                            "Breakfast" -> "e.g. Toast / Eggs / Milk"
                            "Lunch" -> "e.g. Chicken / Rice / Vegetables"
                            "Dinner" -> "e.g. Salmon / Salad / Soup"
                            else -> "e.g. Meal contents"
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { mealInputs[meal] = it },
                            label = { Text("What do you want to eat?") },
                            placeholder = { Text(placeholderText) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            isEditing[meal] = false
                            if (googleUser != null) {
                                val dietPlan = mapOf(
                                    "email" to googleUser.email,
                                    "Breakfast" to if (meal == "Breakfast") inputText else mealInputs["Breakfast"],
                                    "Lunch" to if (meal == "Lunch") inputText else mealInputs["Lunch"],
                                    "Dinner" to if (meal == "Dinner") inputText else mealInputs["Dinner"],
                                    "isCompleted" to false
                                )
                                firestore.collection("diet").document(googleUser.email.toString()).set(
                                    dietPlan
                                )
                            } else if (currentUser != null) {
                                val type: Int = when (meal) {
                                    "Breakfast" -> {
                                        0
                                    }
                                    "Lunch" -> {
                                        1
                                    }
                                    else -> {
                                        2
                                    }
                                }
                                val localDietPlan = DietPlan(
                                    userId = currentUser!!.id,
                                    food = inputText,
                                    mealType = type,
                                    isCompleted = false
                                )
                                dietPlanViewModel.addDietPlan(localDietPlan)
                            }
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

/**
 * Main composable for the Home screen.
 * Displays greeting, quick access to workout and diet plans, and a calendar entry card.
 * Also handles user authentication redirection.
 */
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    drawerState: DrawerState,
    userViewModel: UserViewModel,
    userDetailViewModel: UserDetailViewModel,
    dietPlanViewModel: DietPlanViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var showWorkoutSheet by remember { mutableStateOf(false) }
    var showDietSheet by remember { mutableStateOf(false) }
    val currentUser = Firebase.auth.currentUser
    var showWorkoutSection by remember { mutableStateOf(false) }
    var showDietSection by remember { mutableStateOf(false) }
    var showAllPlan by remember { mutableStateOf(false) }
    var userDetailState by remember { mutableStateOf<UserDetail?>(null) }

    LaunchedEffect(currentUser?.email) {
        userDetailState = userDetailViewModel.queryUserDetailFromCloudDatabase()
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
            GreetingSection(navController = navController)
            PlanCardSection(
                onWorkoutClick = { showWorkoutSection = !showWorkoutSection },
                onDietClick = { showDietSection = !showDietSection },
                onAllPlanClick = { showAllPlan = !showAllPlan }
            )
            AnimatedVisibility(visible = showAllPlan) {
                allPlanSection(userViewModel, workoutPlanViewModel, dietPlanViewModel)
            }

            AnimatedVisibility(visible = showWorkoutSection) {
                TodayWorkoutPlanSection(userViewModel, workoutPlanViewModel)
            }

            AnimatedVisibility(visible = showDietSection) {
                TodayDietPlanSection(userViewModel, dietPlanViewModel)
            }
            Spacer(modifier = Modifier.height(12.dp))
            CalendarEntryCard(navController = navController)
        }
    }
}

/**
 * Section that displays a time-based greeting and a button to explore nearby gyms.
 * Includes a banner image for visual engagement.
 */
@Composable
fun GreetingSection(navController: NavController) {
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
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Want to find a gym nearby? Tap the button to explore the map:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .widthIn(max = 250.dp)
                    .wrapContentHeight()
            )

            OutlinedButton(
                onClick = { navController.navigate("map") },
                border = BorderStroke(1.5.dp, Color(0xFF00C853)),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF00C853),
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = "Home banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

/**
 * Card section labeled "My Plan" that provides two clickable entries:
 * one for the workout plan and one for the diet plan.
 */
@Composable
fun PlanCardSection(
    onWorkoutClick: () -> Unit,
    onDietClick: () -> Unit,
    onAllPlanClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Plan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {onAllPlanClick()}) {
                Text("See all plans")
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onWorkoutClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "\uD83D\uDEB4️",
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center) // ✅ 这里就可以用了
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("My Workout Plan", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDietClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "\uD83C\uDF71️",
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("My Diet Plan", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

/**
 * Motivational card encouraging users to track fitness habits.
 * Provides access to the calendar screen.
 */
@Composable
fun CalendarEntryCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFECB3)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📅 Track your fitness habits",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF795548)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Consistency is the key to progress. Stay on top of your goals by tracking your workouts and meals daily. "
                        + "Building a habit takes time—your calendar helps you stay motivated and accountable throughout the journey.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = { navController.navigate("calendar") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("View Calendar", color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition", "CoroutineCreationDuringComposition")
@Composable
fun allPlanSection(
    userViewModel: UserViewModel,
    workoutPlanViewModel: WorkoutPlanViewModel,
    dietPlanViewModel: DietPlanViewModel
) {
    val currentUser by userViewModel.loginUser.collectAsState()
    val googleUser = Firebase.auth.currentUser
    val firestore = FirebaseFirestore.getInstance()
    val coroutineScope = rememberCoroutineScope()

    // State to hold Firestore data
    var dietData by remember { mutableStateOf<Triple<String?, String?, String?>?>(null) } // Breakfast, Lunch, Dinner
    var workoutData by remember { mutableStateOf<Quadruple<List<Any>?, List<Any>?, List<Any>?, List<Any>?>?>(null) } // completedSets, content, targetSets, process
    var isLoading by remember { mutableStateOf(false) }

    // Fetch Firestore data if googleUser exists
    LaunchedEffect(googleUser) {
        if (googleUser != null) {
            isLoading = true
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val workoutPlansSnapshot = firestore.collection("workout")
                        .document(googleUser.email.toString()).get().await()
                    val dietPlansSnapshot = firestore.collection("diet")
                        .document(googleUser.email.toString()).get().await()

                    // Extract diet data
                    val breakfast = dietPlansSnapshot.getString("Breakfast")
                    val dinner = dietPlansSnapshot.getString("Dinner")
                    val lunch = dietPlansSnapshot.getString("Lunch")
                    dietData = Triple(breakfast, lunch, dinner)

                    // Extract workout data (assuming lists, adjust types as needed)
                    val completedSetsList = workoutPlansSnapshot.get("completedSets") as? List<Any>
                    val contentList = workoutPlansSnapshot.get("content") as? List<Any>
                    val targetSetsList = workoutPlansSnapshot.get("targetSets") as? List<Any>
                    val processList = workoutPlansSnapshot.get("process") as? List<Any>
                    workoutData = Quadruple(completedSetsList, contentList, targetSetsList, processList)
                } catch (e: Exception) {

                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Set ViewModel user IDs if currentUser exists
    if (currentUser != null) {
        workoutPlanViewModel.setUserId(currentUser!!.id)
        dietPlanViewModel.setUserId(currentUser!!.id)
    }

    // Collect ViewModel data
    val allWorkoutPlans by workoutPlanViewModel.allWorkoutPlans.collectAsState()
    val allDietPlans by dietPlanViewModel.allDietPlans.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Loading indicator for Firestore data
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            // Display Firestore data if available
            if (googleUser != null && dietData != null && workoutData != null) {
                Text(
                    text = "Diet Plan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DietPlanCard(dietData!!)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Workout Plan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                WorkoutPlanCard(workoutData!!)
            }
            // Display ViewModel data if available
            else if (currentUser != null && (allWorkoutPlans.isNotEmpty() || allDietPlans.isNotEmpty())) {
                AllPlanSectionViewModelDisplay(
                    allDietPlans = allDietPlans,
                    allWorkoutPlans = allWorkoutPlans
                )
            } else {
                // Fallback when no data is available
                Text(
                    text = "No plans available. Please log in or create a plan.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

// Composable for displaying diet plan
@Composable
fun DietPlanCard(dietData: Triple<String?, String?, String?>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            dietData.first?.let {
                Text(text = "Breakfast: $it", style = MaterialTheme.typography.bodyMedium)
            }
            dietData.second?.let {
                Text(text = "Lunch: $it", style = MaterialTheme.typography.bodyMedium)
            }
            dietData.third?.let {
                Text(text = "Dinner: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// Composable for displaying workout plan
@Composable
fun WorkoutPlanCard(workoutData: Quadruple<List<Any>?, List<Any>?, List<Any>?, List<Any>?>) {
    // Determine the number of cards based on the shortest non-null list length
    val lengths = listOfNotNull(
        workoutData.first?.size,
        workoutData.second?.size,
        workoutData.third?.size,
        workoutData.fourth?.size
    )
    val minLength = if (lengths.isNotEmpty()) lengths.min() else 0

    if (minLength == 0) {
        // Display a message if no data is available
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "No workout data available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    // Create a card for each index
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (index in 0 until minLength) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    workoutData.second?.getOrNull(index)?.let { content ->
                        Text(
                            text = "Content: $content",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    workoutData.third?.getOrNull(index)?.let { targetSet ->
                        Text(
                            text = "Target Sets: $targetSet",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    workoutData.first?.getOrNull(index)?.let { completedSet ->
                        Text(
                            text = "Completed Sets: $completedSet",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    workoutData.fourth?.getOrNull(index)?.let { process ->
                        // Convert process to percentage with two decimal places
                        val percentage = when (process) {
                            is Number -> process.toDouble() * 100
                            is String -> process.toDoubleOrNull()?.times(100) ?: 0.0
                            else -> 0.0
                        }
                        Text(
                            text = "Process: ${String.format("%.2f%%", percentage)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// Helper data class for workout data (since Kotlin doesn't have a built-in Quadruple)
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@Composable
fun AllPlanSectionViewModelDisplay(
    allDietPlans: List<DietPlan>,
    allWorkoutPlans: List<WorkoutPlan>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (allDietPlans.isNotEmpty()) {
            Text(
                text = "Diet Plan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            allDietPlans.forEach { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Meal: ${plan.food}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Type: ${
                                when (plan.mealType) {
                                    0 -> "Breakfast"
                                    1 -> "Lunch"
                                    2 -> "Dinner"
                                    else -> "Unknown (${plan.mealType})"
                                }
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (allWorkoutPlans.isNotEmpty()) {
            Text(
                text = "Workout Plan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            allWorkoutPlans.forEach { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Content: ${plan.content}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Target Sets: ${plan.targetSets}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Completed Sets: ${plan.completedSets}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Progress: ${String.format("%.2f%%", plan.progress * 100)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


/**
 * Composable for the top app bar including a menu icon and page title.
 * Used across screens for consistent navigation.
 */
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

/**
 * Preview of the HomeScreen composable for use in Android Studio design tools.
 */
@Preview
@Composable
fun HomeScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val application = Application()
    val userViewModel = UserViewModel(application)
    val userDetailViewModel = UserDetailViewModel(application)
    val dietPlanViewModel = DietPlanViewModel(application = Application())
    val workoutPlanViewModel = WorkoutPlanViewModel(application = Application())

    HomeScreen(
        navController,
        drawerState,
        userViewModel,
        userDetailViewModel,
        dietPlanViewModel,
        workoutPlanViewModel
    )
}