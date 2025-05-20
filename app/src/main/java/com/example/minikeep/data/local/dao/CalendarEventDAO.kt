package com.example.minikeep.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.minikeep.data.local.entity.CalendarEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDAO {

    @Query("select * from calendar_event where userId = :userId")
    fun getAllCalendarEventByUserId(userId: Int): Flow<List<CalendarEvent>>

    @Insert
    suspend fun insertCalendarEvent(calendarEvent: CalendarEvent)

    @Update
    suspend fun updateCalendarEvent(calendarEvent: CalendarEvent)

    @Delete
    suspend fun deleteCalendarEvent(calendarEvent: CalendarEvent)

}