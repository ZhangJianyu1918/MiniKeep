package com.example.minikeep.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    var date by remember { mutableStateOf("") }
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val coroutineScope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }

    // Mutable state for height, weight, and gender
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }

    // State for gender dropdown menu expansion
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Form") },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxWidth(),
        ) {
            // Height input
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                label = { Text("Height") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Weight input
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                label = { Text("Weight") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Date input with clickable trigger for date picker
            TextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
                    .padding(16.dp),
                label = { Text("Date") },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = com.example.minikeep.R.drawable.ic_launcher_background),
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { showDatePicker = true }.size(40.dp)
                    )
                }
            )

            // Date Picker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = {
                        showDatePicker = false
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                            selectedDate = datePickerState.selectedDateMillis!!
                            date = "DoB: ${formatter.format(Date(selectedDate))}"
                        }) {
                            Text("Ok")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDatePicker = false
                        }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(
                        state = datePickerState
                    )
                }
            }

            // Gender dropdown menu
            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    label = { Text("Gender") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .padding(16.dp),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                genderExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
