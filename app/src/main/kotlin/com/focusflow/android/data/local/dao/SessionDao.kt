package com.focusflow.android.data.local.dao

import androidx.room.*
import com.focusflow.android.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY start_time DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE start_time >= :fromEpochMs ORDER BY start_time DESC")
    fun getSessionsSince(fromEpochMs: Long): Flow<List<SessionEntity>>

    @Query("SELECT SUM(actual_duration_ms) FROM sessions WHERE start_time >= :fromEpochMs AND was_completed = 1")
    fun getTotalFocusTimeSince(fromEpochMs: Long): Flow<Long?>

    @Query("SELECT COUNT(*) FROM sessions WHERE start_time >= :fromEpochMs")
    fun getSessionCountSince(fromEpochMs: Long): Flow<Int>

    @Upsert
    suspend fun upsertSession(session: SessionEntity): Long

    @Query("UPDATE sessions SET end_time = :endTime, actual_duration_ms = :actualDurationMs, was_completed = :wasCompleted WHERE id = :id")
    suspend fun completeSession(id: Long, endTime: Long, actualDurationMs: Long, wasCompleted: Boolean)

    @Query("UPDATE sessions SET escape_attempts = escape_attempts + 1 WHERE id = :id")
    suspend fun incrementEscapeAttempts(id: Long)
}
