package com.focusflow.android.domain.usecase.calendar

import com.focusflow.android.domain.model.CalendarEvent
import com.focusflow.android.domain.repository.CalendarRepository
import javax.inject.Inject

class GetUpcomingEventsUseCase @Inject constructor(
    private val repository: CalendarRepository
) {
    suspend operator fun invoke(): List<CalendarEvent> = repository.getUpcomingEvents()
}
