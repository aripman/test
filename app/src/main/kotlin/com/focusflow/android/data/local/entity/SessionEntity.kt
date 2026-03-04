package com.focusflow.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "mode_id") val modeId: Long,
    @ColumnInfo(name = "mode_name") val modeName: String,
    @ColumnInfo(name = "start_time") val startTime: Long,
    @ColumnInfo(name = "end_time") val endTime: Long?,
    @ColumnInfo(name = "planned_duration_ms") val plannedDurationMs: Long,
    @ColumnInfo(name = "actual_duration_ms") val actualDurationMs: Long?,
    @ColumnInfo(name = "was_completed") val wasCompleted: Boolean,
    @ColumnInfo(name = "escape_attempts") val escapeAttempts: Int
)
