package com.focusflow.android.domain.model

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val allDay: Boolean,
    val calendarColor: Int,
    val location: String? = null
)
