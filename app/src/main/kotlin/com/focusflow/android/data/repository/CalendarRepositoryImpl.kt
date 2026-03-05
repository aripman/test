package com.focusflow.android.data.repository

import android.content.ContentResolver
import android.provider.CalendarContract
import com.focusflow.android.domain.model.CalendarEvent
import com.focusflow.android.domain.repository.CalendarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : CalendarRepository {

    override suspend fun getUpcomingEvents(fromMs: Long, toMs: Long): List<CalendarEvent> =
        withContext(Dispatchers.IO) { queryEvents(fromMs, toMs) }

    override fun observeUpcomingEvents(): Flow<List<CalendarEvent>> = flow {
        val now = System.currentTimeMillis()
        emit(queryEvents(now, now + 7 * 24 * 60 * 60 * 1000L))
    }.flowOn(Dispatchers.IO)

    private fun queryEvents(fromMs: Long, toMs: Long): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.CALENDAR_COLOR,
            CalendarContract.Events.EVENT_LOCATION
        )
        val selection = "${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
        val selectionArgs = arrayOf(fromMs.toString(), toMs.toString())
        val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

        try {
            contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    events += CalendarEvent(
                        id = cursor.getLong(0),
                        title = cursor.getString(1) ?: "Untitled",
                        startTime = cursor.getLong(2),
                        endTime = cursor.getLong(3),
                        allDay = cursor.getInt(4) == 1,
                        calendarColor = cursor.getInt(5),
                        location = cursor.getString(6)
                    )
                }
            }
        } catch (e: SecurityException) {
            // READ_CALENDAR permission not granted
        }
        return events
    }
}
