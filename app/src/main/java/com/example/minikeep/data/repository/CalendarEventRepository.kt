package com.example.minikeep.data.repository

import android.app.Application
import com.example.minikeep.data.db.MiniKeepDatabase
import com.example.minikeep.data.local.dao.CalendarEventDAO
import com.example.minikeep.data.local.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow


class CalendarEventRepository(application: Application) {

    private val calendarEventDAO: CalendarEventDAO =
        MiniKeepDatabase.getDatabase(application).calendarEventDao()


    fun getAllCalendarEvents(userId: Int): Flow<List<CalendarEvent>> {
        return calendarEventDAO.getAllCalendarEventByUserId(userId)
    }

    suspend fun insertCalendarEvent(calendarEvent: CalendarEvent) {
        calendarEventDAO.insertCalendarEvent(calendarEvent)
    }

    suspend fun updateCalendarEvent(calendarEvent: CalendarEvent) {
        calendarEventDAO.updateCalendarEvent(calendarEvent)
    }
}