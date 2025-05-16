package com.example.minikeep.data.local.entity

data class CalendarEvent(
    val summary: String,
    val location: String,
    val description: String,
    val start: EventDateTime,
    val end: EventDateTime
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String
)
