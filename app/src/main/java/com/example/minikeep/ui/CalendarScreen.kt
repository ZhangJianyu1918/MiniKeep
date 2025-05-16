package com.example.minikeep.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
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
fun CalendarScreen(navController: NavController, drawerState: DrawerState) {
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
//    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var events by remember { mutableStateOf<List<CalendarEvent>>(emptyList()) }
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    val account = GoogleSignIn.getLastSignedInAccount(context)
//    val googleCalendarService = account?.let { getCalendarService(context) }
//    val authorizationLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            // 用户已授权，重新尝试获取事件
//            account?.let {
//                fetchCalendarEvents(context, it, onResult = { events = it }, onNeedAuthorization = { })
//            }
//        } else {
//            Toast.makeText(context, "日历授权被拒绝", Toast.LENGTH_SHORT).show()
//        }
//    }
    val authorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            account?.let {
                fetchCalendarEvents(
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
    LaunchedEffect(account) {
        account?.let {
            service = getCalendarService(context, it)
            fetchCalendarEvents(
                context = context,
                account = it,
                onResult = { fetchedEvents -> events = fetchedEvents },
                onNeedAuthorization = { exception ->
                    authorizationLauncher.launch(exception.intent)
                }
            )
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
        floatingActionButtonPosition = FabPosition.Start
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 显示月份标题
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.UK) + " ${currentMonth.year}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // 显示星期标题
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
                columns = GridCells.Fixed(7), // 7 列代表一周
                modifier = Modifier.fillMaxWidth()
            ) {
                // 填充月初的空位
                items(firstDayOffset) {
                    Box(modifier = Modifier.size(48.dp)) // 空位占位符
                }

                // 填充日期
                items(daysInMonth) { dayIndex ->
                    val date = firstDayOfMonth.plusDays(dayIndex.toLong())

//                    val hasEvent = events.any { it.start == date }
                    val hasEvent = false
                    CalendarDay(
                        day = date.dayOfMonth,
                        hasEvent = hasEvent,
                        isToday = date == LocalDate.now()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 添加 LazyColumn 显示事件列表
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
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // TODO 你可以在这里处理添加事件的逻辑
                       insertEvent(context, account, eventTitle, eventBeginDate, eventEndDate)
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

fun getCalendarService(context: Context, account: GoogleSignInAccount): Calendar {
    val credential = GoogleAccountCredential.usingOAuth2(
        context, listOf(CalendarScopes.CALENDAR)
    )
    credential.selectedAccount = account.account

    return Calendar.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        credential
    ).setApplicationName("MiniKeep").build()
}

@RequiresApi(Build.VERSION_CODES.O)
fun fetchCalendarEvents(
    context: Context,
    account: GoogleSignInAccount,
//    onResult: (Events?) -> Unit,
    onResult: (List<CalendarEvent>) -> Unit,
    onNeedAuthorization: (UserRecoverableAuthIOException) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val service = getCalendarService(context, account)
            val now = System.currentTimeMillis()
            // Set timeMin to 7 days before now
            val timeMin = DateTime(now - 7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds
            // Set timeMax to 7 days after now
            val timeMax = DateTime(now + 7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds
            val events = service?.events()?.list("primary")
                ?.setTimeMin(timeMin)
                ?.setTimeMax(timeMax)
                ?.setOrderBy("startTime")
                ?.setSingleEvents(true)
                ?.execute()
            val calendarEvents = events?.items?.mapNotNull { event ->
                val startDateTime = event.start?.dateTime?.value?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                } ?: return@mapNotNull null
                val endDateTime = event.end?.dateTime?.value?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
                } ?: return@mapNotNull null
                CalendarEvent(
                    id = event.id ?: "",
                    summary = event.summary ?: "No Title",
                    start = startDateTime,
                    end = endDateTime
                )
            } ?: emptyList()
            println("calendarEvents: $calendarEvents")
            withContext(Dispatchers.Main) {
//                onResult(events)
                onResult(calendarEvents)
            }
        } catch (e: UserRecoverableAuthIOException) {
            // 需要授权，切到主线程执行 launcher
            withContext(Dispatchers.Main) {
                onNeedAuthorization(e)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}

fun insertEvent(
    context: Context,
    account: GoogleSignInAccount?,
    eventTitle: String,
    eventBeginDate: String,
    eventEndDate: String
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val service = account?.let { getCalendarService(context, it) }
            if (service != null) {
                service.events()?.insert("primary",
                    Event()
                        .setSummary(eventTitle)
                        .setStart(EventDateTime().setDate(DateTime(eventBeginDate)))
                        .setEnd(EventDateTime().setDate(DateTime(eventEndDate)))
                )?.execute()
                println("InsertEvent 插入事件成功")
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Log.e("InsertEvent", "插入事件失败：${e.message}")
                Toast.makeText(context, "插入事件失败：${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val navController = androidx.navigation.compose.rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    CalendarScreen(navController, drawerState)
}