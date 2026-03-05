package com.focusflow.android.domain.model

sealed class SessionState {
    object Idle : SessionState()

    data class Active(
        val session: FocusSession,
        val mode: FocusMode,
        val remainingMs: Long,
        val elapsedMs: Long,
        val isPaused: Boolean = false
    ) : SessionState()

    data class Ended(
        val session: FocusSession,
        val wasCompleted: Boolean
    ) : SessionState()
}
