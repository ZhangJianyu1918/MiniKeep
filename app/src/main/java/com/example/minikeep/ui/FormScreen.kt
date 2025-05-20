package com.example.minikeep.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.minikeep.data.local.entity.UserDetail
import com.example.minikeep.viewmodel.UserDetailViewModel
import com.example.minikeep.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

fun calculateAgeFromBirth(dateString: String): Int {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = formatter.parse(dateString)!!
        val today = Calendar.getInstance()
        val dob = Calendar.getInstance().apply { time = birthDate }

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        age
    } catch (e: Exception) {
        0
    }
}

fun calculateBMI(heightCm: Int, weightKg: Float): Float {
    val heightM = heightCm / 100f
    return (weightKg / (heightM * heightM)).let { String.format(Locale.getDefault(), "%.1f", it).toFloat() }
}

fun estimateBodyFatPercentage(age: Int, gender: String, bmi: Float): Float {
    val genderConstant = if (gender == "Male") 1 else 0
    val fat = 1.20f * bmi + 0.23f * age - 10.8f * genderConstant - 5.4f
    return String.format(Locale.getDefault(), "%.1f", fat).toFloat()
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormScreen(
    navController: NavController,
    drawerState: DrawerState,
    userDetailViewModel: UserDetailViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())

    var date by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }
    val genderOptions = listOf("Male", "Female")
    val fitnessGoalOptions = listOf("Weight Loss", "Muscle Gain", "General Fitness", "Endurance")
    var genderExpanded by remember { mutableStateOf(false) }
    var fitnessGoalExpanded by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf(0) }
    var bmi by remember { mutableStateOf(0f) }
    var fat by remember { mutableStateOf(0f) }
    var latestUserDetail by remember { mutableStateOf<UserDetail?>(null) }

    val currentUserId = userViewModel.loginUser.collectAsState().value?.id

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            userDetailViewModel.getUserDetailByUserId(currentUserId) { detail ->
                if (detail != null) {
                    height = detail.height.toString()
                    weight = detail.weight.toString()
                    date = detail.birthday
                    gender = detail.gender
                    fitnessGoal = detail.goal
                    age = detail.age

                    bmi = calculateBMI(detail.height, detail.weight)
                    fat = estimateBodyFatPercentage(detail.age, detail.gender, bmi)
                    latestUserDetail = detail
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MiniKeepTopBar("Personal Form", drawerState, coroutineScope, Modifier)
        },
        modifier = Modifier.systemBarsPadding(),
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            item {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
            }
            item {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date of Birth") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { showDatePicker = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    }
                )
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                                selectedDate = datePickerState.selectedDateMillis!!
                                date = formatter.format(Date(selectedDate))
                            }) { Text("OK") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded },
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        genderOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    gender = it
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = fitnessGoalExpanded,
                    onExpandedChange = { fitnessGoalExpanded = !fitnessGoalExpanded },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                ) {
                    OutlinedTextField(
                        value = fitnessGoal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fitness Goal") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fitnessGoalExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = fitnessGoalExpanded,
                        onDismissRequest = { fitnessGoalExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        fitnessGoalOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    fitnessGoal = it
                                    fitnessGoalExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val heightValue = height.toIntOrNull()
                        val weightValue = weight.toFloatOrNull()

                        if (
                            currentUserId != null &&
                            heightValue != null && heightValue in 30..300 &&
                            weightValue != null && weightValue in 30f..300f &&
                            date.isNotBlank() && gender.isNotBlank() && fitnessGoal.isNotBlank()
                        ) {
                            age = calculateAgeFromBirth(date)
                            bmi = calculateBMI(heightValue, weightValue)
                            fat = estimateBodyFatPercentage(age, gender, bmi)

                            val userDetail = UserDetail(
                                userId = currentUserId,
                                age = age,
                                height = heightValue,
                                weight = weightValue,
                                birthday = date,
                                gender = gender,
                                goal = fitnessGoal
                            )
                            userDetailViewModel.upsertUserDetail(userDetail)
                            latestUserDetail = userDetail
                            Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please fill in all valid fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Text("Submit")
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                latestUserDetail?.let {
                    FormResultCard(it)
                }
            }
        }
    }
}


@Composable
fun FormResultCard(userDetail: UserDetail) {
    val bmi = calculateBMI(userDetail.height, userDetail.weight)
    val fat = estimateBodyFatPercentage(userDetail.age, userDetail.gender, bmi)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Your Current Health Index:", style = MaterialTheme.typography.titleLarge)
            Text("Your Age: ${userDetail.age}", style = MaterialTheme.typography.bodyLarge)
            Text("Your BMI: $bmi", style = MaterialTheme.typography.bodyLarge)
            Text("Your Best BMI Range is: [20, 25]", style = MaterialTheme.typography.bodyLarge)
            Text("Your Body Fat Percentage: $fat%", style = MaterialTheme.typography.bodyLarge)

            val diet = when {
                bmi < 18.5 -> "Increase protein and calories"
                bmi < 25 -> "Maintain a balanced diet"
                bmi < 30 -> "Low-sugar, high-fiber meals"
                else -> "Controlled calories with cardio focus"
            }

            val fitness = when {
                bmi < 18.5 -> "Strength training and mass gain"
                bmi < 25 -> "General cardio and strength mix"
                bmi < 30 -> "Weight loss + aerobic focus"
                else -> "Intensive cardio with strength circuit"
            }

            Text("Recommend Diet plan: $diet", style = MaterialTheme.typography.bodyLarge)
            Text("Recommend Fitness plan: $fitness", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
