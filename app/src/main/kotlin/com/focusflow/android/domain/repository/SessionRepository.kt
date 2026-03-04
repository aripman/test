package com.focusflow.android.domain.repository

import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.FocusSession
import com.focusflow.android.domain.model.SessionState
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getSessionHistory(): Flow<List<FocusSession>>
    fun getTodayFocusTimeMs(): Flow<Long>
    fun getTodaySessionCount(): Flow<Int>
    fun observeSessionState(): Flow<SessionState>
    suspend fun startSession(mode: FocusMode): Long
    suspend fun endSession(sessionId: Long, wasCompleted: Boolean)
    suspend fun pauseSession()
    suspend fun resumeSession()
    suspend fun incrementEscapeAttempts(sessionId: Long)
}
