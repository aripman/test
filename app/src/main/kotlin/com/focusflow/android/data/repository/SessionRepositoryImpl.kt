package com.focusflow.android.data.repository

import com.focusflow.android.data.datastore.SessionStateDataStore
import com.focusflow.android.data.local.dao.SessionDao
import com.focusflow.android.data.local.entity.SessionEntity
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.FocusSession
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionStateDataStore: SessionStateDataStore
) : SessionRepository {

    override fun getSessionHistory(): Flow<List<FocusSession>> =
        sessionDao.getAllSessions().map { entities -> entities.map { it.toDomain() } }

    override fun getTodayFocusTimeMs(): Flow<Long> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return sessionDao.getTotalFocusTimeSince(startOfDay).map { it ?: 0L }
    }

    override fun getTodaySessionCount(): Flow<Int> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return sessionDao.getSessionCountSince(startOfDay)
    }

    override fun observeSessionState(): Flow<SessionState> =
        sessionStateDataStore.sessionStateFlow

    override suspend fun startSession(mode: FocusMode): Long {
        val entity = SessionEntity(
            modeId = mode.id,
            modeName = mode.name,
            startTime = System.currentTimeMillis(),
            endTime = null,
            plannedDurationMs = mode.durationMinutes * 60_000L,
            actualDurationMs = null,
            wasCompleted = false,
            escapeAttempts = 0
        )
        val id = sessionDao.upsertSession(entity)
        sessionStateDataStore.setActiveSession(id, mode)
        return id
    }

    override suspend fun endSession(sessionId: Long, wasCompleted: Boolean) {
        val endTime = System.currentTimeMillis()
        val sessions = sessionDao.getAllSessions().first()
        val startTime = sessions.find { it.id == sessionId }?.startTime ?: endTime
        sessionDao.completeSession(
            id = sessionId,
            endTime = endTime,
            actualDurationMs = endTime - startTime,
            wasCompleted = wasCompleted
        )
        sessionStateDataStore.clearSession()
    }

    override suspend fun pauseSession() = sessionStateDataStore.setPaused(true)

    override suspend fun resumeSession() = sessionStateDataStore.setPaused(false)

    override suspend fun incrementEscapeAttempts(sessionId: Long) =
        sessionDao.incrementEscapeAttempts(sessionId)

    private fun SessionEntity.toDomain() = FocusSession(
        id = id,
        modeId = modeId,
        modeName = modeName,
        startTime = startTime,
        endTime = endTime,
        plannedDurationMs = plannedDurationMs,
        actualDurationMs = actualDurationMs,
        wasCompleted = wasCompleted,
        escapeAttempts = escapeAttempts
    )
}
