package com.example.minikeep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "calendar_event",foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class CalendarEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userId:  Int,
    val summary: String,
    val start: String,
    val end: String,
    val isFinished: Boolean
)


