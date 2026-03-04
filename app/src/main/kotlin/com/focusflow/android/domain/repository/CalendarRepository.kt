package com.focusflow.android.domain.repository

import com.focusflow.android.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    suspend fun getUpcomingEvents(
        fromMs: Long = System.currentTimeMillis(),
        toMs: Long = fromMs + 7 * 24 * 60 * 60 * 1000L
    ): List<CalendarEvent>

    fun observeUpcomingEvents(): Flow<List<CalendarEvent>>
}
