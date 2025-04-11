package com.example.minikeep.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FormScreen(navController: NavController, drawerState: DrawerState) {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val coroutineScope = rememberCoroutineScope()

    var date by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = Instant.now().toEpochMilli())
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }

    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var fitnessGoal by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female")
    val fitnessGoalOptions = listOf("Weight Loss", "Muscle Gain", "General Fitness", "Endurance")

    var genderExpanded by remember { mutableStateOf(false) }
    var fitnessGoalExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Personal Form",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding(),
    ) { padding ->
        LazyColumn (
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { showDatePicker = true },
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
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                        },
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
                    onExpandedChange = {
                        fitnessGoalExpanded = !fitnessGoalExpanded
                        Log.d("Dropdown", "Expanded: $fitnessGoalExpanded")
                    },
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                ) {
                    OutlinedTextField(
                        value = fitnessGoal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Goal") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = fitnessGoalExpanded)
                        },
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
                FormResultCard()
            }
            item {
                Button(
                    onClick = ({})
                ) {
                    Text("Submit")
                }
            }
        }
    }
}


@Composable
fun FormResultCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your Current Health Index:",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Your BMI: 28",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Your Best BMI Range is: [20, 25]",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Recommend Diet plan: ",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Recommend Fitness plan: ",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}