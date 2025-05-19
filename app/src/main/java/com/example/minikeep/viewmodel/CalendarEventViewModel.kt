package com.example.minikeep.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minikeep.data.local.entity.CalendarEvent
import com.example.minikeep.data.local.entity.User
import com.example.minikeep.data.repository.CalendarEventRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

class CalendarEventViewModel(application: Application): AndroidViewModel(application) {

    private val calendarEventRepository: CalendarEventRepository = CalendarEventRepository(application)

    fun getAllCalendarEventByUserId(userId: Int): Flow<List<CalendarEvent>> {
        return calendarEventRepository.getAllCalendarEvents(userId)
    }

    fun insertCalendarEvent(calendarEvent: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            calendarEventRepository.insertCalendarEvent(calendarEvent)
        }
    }

    fun updateCalendarEvent(calendarEvent: CalendarEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            calendarEventRepository.updateCalendarEvent(calendarEvent)
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
                        summary = event.summary ?: "No Title",
                        start = startDateTime.toString(),
                        end = endDateTime.toString(),
                        isFinished = true,
                        userId = 1,
                        id = 1
                    )
                } ?: emptyList()
                println("calendarEvents: $calendarEvents")
                withContext(Dispatchers.Main) {
                    onResult(calendarEvents)
                }
            } catch (e: UserRecoverableAuthIOException) {
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
        eventEndDate: String,
        currentUser: User?
    ) {
        if (currentUser != null) {
            insertCalendarEvent(CalendarEvent(
                userId = currentUser.id,
                summary = eventTitle,
                start = eventBeginDate,
                end = eventEndDate,
                isFinished = false
            ))
            return
        }
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
                withContext(context = Dispatchers.Main) {
                    Log.e("InsertEvent", "插入事件失败：${e.message}")
                    Toast.makeText(context, "插入事件失败：${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}