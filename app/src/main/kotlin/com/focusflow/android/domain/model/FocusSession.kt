package com.focusflow.android.domain.model

data class FocusSession(
    val id: Long = 0,
    val modeId: Long,
    val modeName: String,
    val startTime: Long,
    val endTime: Long? = null,
    val plannedDurationMs: Long,
    val actualDurationMs: Long? = null,
    val wasCompleted: Boolean = false,
    val escapeAttempts: Int = 0
)
