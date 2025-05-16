package com.example.minikeep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

//@Entity(tableName = "calendar_event",foreignKeys = [
//    ForeignKey(
//        entity = User::class,
//        parentColumns = ["id"],
//        childColumns = ["id"],
//        onDelete = ForeignKey.CASCADE
//    )
//])
data class CalendarEvent(
//    @PrimaryKey val id: Int,
    val id: String,
    val summary: String,
    val start: LocalDateTime,
    val end: LocalDateTime
)


