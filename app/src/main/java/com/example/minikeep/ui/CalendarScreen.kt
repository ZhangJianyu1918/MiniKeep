package com.example.minikeep.ui


import android.content.Context
import android.os.Build
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.api.services.calendar.model.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class MockEvent(
    val summary: String,
    val start: String,
    val end: String
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(navController: NavController, drawerState: DrawerState) {
    val coroutineScope = rememberCoroutineScope()
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOffset = firstDayOfMonth.dayOfWeek.value % 7

    val mockEvents = listOf(
        MockEvent("Back Day", "2025-04-09T10:00:00", "2025-04-09T11:00:00"),
        MockEvent("Chest Day", "2025-04-11T12:00:00", "2025-04-11T13:00:00"),
        MockEvent("Leg Day", "2025-04-12T14:00:00", "2025-04-12T15:30:00")
    )

    // 模拟一些事件
    val events = listOf(
        CalendarEvent(LocalDate.now(), "Meeting"),
        CalendarEvent(LocalDate.now().plusDays(2), "Launch"),
        CalendarEvent(LocalDate.now().plusDays(3), "Launch")
    )

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
                    val hasEvent = events.any { it.date == date }
                    CalendarDay(
                        day = date.dayOfMonth,
                        hasEvent = hasEvent,
                        isToday = date == LocalDate.now()
                    )
                }
            }
            if (mockEvents.isEmpty()) {
                Text(text = "No events", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn {
                    items(mockEvents) { event ->
                        EventCard(event)
                    }
                }
            }
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

data class CalendarEvent(val date: LocalDate, val title: String)


private fun createGoogleCalendarEvent(context: Context, event: Event) {
    // 启动一个协程来运行网络请求
    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val credential = GoogleAccountCredential.usingOAuth2(
//                , listOf(CalendarScopes.CALENDAR)
//            )
//            credential.selectedAccount = GoogleSignIn.getLastSignedInAccount(this@MainActivity)?.account
//
//            val service = Calendar.Builder(
//                AndroidHttp.newCompatibleTransport(),
//                JacksonFactory.getDefaultInstance(),
//                credential
//            ).setApplicationName("Your App Name").build()
//
//            val event = Event().apply {
//                summary = "测试会议"
//                location = "虚拟会议室"
//                description = "测试描述"
//                start = EventDateTime().setDateTime(DateTime("2025-05-10T10:00:00+08:00"))
//                end = EventDateTime().setDateTime(DateTime("2025-05-10T11:00:00+08:00"))
//            }
//
//            service.events().insert("primary", event).execute()
//            Log.d("Calendar", "事件添加成功")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("Calendar", "事件添加失败: ${e.message}")
//        }
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