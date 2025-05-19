package com.example.minikeep.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.minikeep.data.local.entity.CalendarEvent
import com.example.minikeep.viewmodel.CalendarEventViewModel
import com.example.minikeep.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.services.calendar.Calendar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


data class MockEvent(
    val summary: String,
    val start: String,
    val end: String
)

@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    navController: NavController,
    drawerState: DrawerState,
    calendarEventViewModel: CalendarEventViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOffset = firstDayOfMonth.dayOfWeek.value % 7
    var showDialog by remember { mutableStateOf(false) }
    var eventTitle by remember { mutableStateOf("") }
    var eventBeginDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var eventEndDate by remember { mutableStateOf(LocalDate.now().toString()) }
    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val account = GoogleSignIn.getLastSignedInAccount(context)
    val currentUser by userViewModel.loginUser.collectAsState()
    val authorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            account?.let {
                calendarEventViewModel.fetchCalendarEvents(
                    context = context,
                    account = it,
                    onResult = { fetchedEvents -> events = fetchedEvents },
                    onNeedAuthorization = { exception ->
//                        authorizationLauncher.launch(exception.intent)
                    }
                )
            }
        } else {
            Toast.makeText(context, "日历授权被拒绝", Toast.LENGTH_SHORT).show()
        }
    }
    var service: Calendar
    var eventBeginDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    var eventEndDateTime by remember { mutableStateOf(LocalDateTime.now().plusHours(1)) }

    LaunchedEffect(userViewModel.loginUser) {
        if (userViewModel.loginUser.value == null && Firebase.auth.currentUser == null) {
            navController.navigate("login")
        }
    }

    LaunchedEffect(account) {
        account?.let {
            service = calendarEventViewModel.getCalendarService(context, it)
            calendarEventViewModel.fetchCalendarEvents(
                context = context,
                account = it,
                onResult = { fetchedEvents -> events = fetchedEvents },
                onNeedAuthorization = { exception ->
                    authorizationLauncher.launch(exception.intent)
                }
            )
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            calendarEventViewModel.getAllCalendarEventByUserId(it.id).collectLatest {
                events = it
            }
        }
    }

    Scaffold(
        topBar = {
            MiniKeepTopBar(
                "Calendar",
                drawerState = drawerState,
                coroutineScope = coroutineScope,
                modifier = Modifier
            )
        },
        modifier = Modifier.systemBarsPadding(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .size(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event",
                    modifier = Modifier
                        .size(56.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.UK) + " ${currentMonth.year}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(firstDayOffset) {
                    Box(modifier = Modifier.size(48.dp))
                }
                items(daysInMonth) { dayIndex ->
                    val date = firstDayOfMonth.plusDays(dayIndex.toLong())
                    val hasEvent = false
                    CalendarDay(
                        day = date.dayOfMonth,
                        hasEvent = hasEvent,
                        isToday = date == LocalDate.now()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Events",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(items = events) { event ->
                    EventCard(event)
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Calendar Event") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = eventTitle,
                            onValueChange = { eventTitle = it },
                            label = { Text("Event Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = eventBeginDate,
                            onValueChange = { eventBeginDate = it },
                            label = { Text("Event Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = eventEndDate,
                            onValueChange = { eventEndDate = it },
                            label = { Text("Event Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth()
                        )
//                        DatePickerField(
//                            label = "Start Date",
//                            selectedDate = eventBeginDate,
//                            onDateSelected = { eventBeginDate = it }
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        DatePickerField(
//                            label = "End Date",
//                            selectedDate = eventEndDate,
//                            onDateSelected = { eventEndDate = it }
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                       calendarEventViewModel.insertEvent(context, account, eventTitle, eventBeginDate, eventEndDate, currentUser)
                        showDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun EventCard(event: CalendarEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.summary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start: ${event.start}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "End: ${event.end}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun EventCard(event: MockEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.summary,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start: ${event.start}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "End: ${event.end}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
@Composable
fun CalendarDay(day: Int, hasEvent: Boolean, isToday: Boolean) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { /* 可添加点击事件 */ },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.Red, CircleShape)
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = android.icu.util.Calendar.getInstance()
    val year = calendar.get(android.icu.util.Calendar.YEAR)
    val month = calendar.get(android.icu.util.Calendar.MONTH)
    val day = calendar.get(android.icu.util.Calendar.DAY_OF_MONTH)

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        android.app.DatePickerDialog(
            context,
            { _, pickedYear, pickedMonth, pickedDay ->
                val pickedDate =
                    String.format("%04d-%02d-%02d", pickedYear, pickedMonth + 1, pickedDay)
                onDateSelected(pickedDate)
                showDialog = false
            },
            year, month, day
        ).show()
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {}, // 不允许手动编辑
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        readOnly = true,
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Pick date")
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val application = Application()
    val calendarEventViewModel = CalendarEventViewModel(application)
    val userViewModel = UserViewModel(application)
    CalendarScreen(navController, drawerState, calendarEventViewModel, userViewModel)
}