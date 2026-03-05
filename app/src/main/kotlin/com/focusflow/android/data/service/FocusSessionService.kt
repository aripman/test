package com.focusflow.android.data.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.focusflow.android.FocusFlowApp
import com.focusflow.android.MainActivity
import com.focusflow.android.core.util.TimeFormatter
import com.focusflow.android.data.datastore.SessionStateDataStore
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.repository.SessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class FocusSessionService : Service() {

    @Inject lateinit var sessionStateDataStore: SessionStateDataStore
    @Inject lateinit var sessionRepository: SessionRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null

    companion object {
        const val ACTION_START = "com.focusflow.START_SESSION"
        const val ACTION_STOP = "com.focusflow.STOP_SESSION"
        const val ACTION_PAUSE_TOGGLE = "com.focusflow.PAUSE_TOGGLE"
        const val NOTIFICATION_ID = 1001

        fun startIntent(context: Context) = Intent(context, FocusSessionService::class.java).apply {
            action = ACTION_START
        }

        fun stopIntent(context: Context) = Intent(context, FocusSessionService::class.java).apply {
            action = ACTION_STOP
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, buildNotification("Starting focus session…"))
                startTimer()
            }
            ACTION_STOP -> stopSession()
            ACTION_PAUSE_TOGGLE -> togglePause()
        }
        return START_STICKY
    }

    private fun startTimer() {
        timerJob = serviceScope.launch {
            sessionStateDataStore.sessionStateFlow.collect { state ->
                when {
                    state is SessionState.Active && !state.isPaused -> {
                        delay(1_000L)
                        val newRemaining = state.remainingMs - 1_000L
                        if (newRemaining <= 0) {
                            sessionRepository.endSession(state.session.id, wasCompleted = true)
                            stopSession()
                        } else {
                            sessionStateDataStore.updateRemainingTime(newRemaining)
                            updateNotification(newRemaining)
                        }
                    }
                    state is SessionState.Active && state.isPaused -> {
                        updateNotification(state.remainingMs, paused = true)
                    }
                    state is SessionState.Idle -> stopSession()
                    else -> {}
                }
            }
        }
    }

    private fun togglePause() {
        serviceScope.launch {
            val state = sessionStateDataStore.sessionStateFlow.first()
            if (state is SessionState.Active) {
                if (state.isPaused) sessionRepository.resumeSession()
                else sessionRepository.pauseSession()
            }
        }
    }

    private fun stopSession() {
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(remainingMs: Long, paused: Boolean = false) {
        val text = if (paused) "Paused — ${TimeFormatter.formatMs(remainingMs)} remaining"
                   else "${TimeFormatter.formatMs(remainingMs)} remaining"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(contentText: String): Notification {
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            stopIntent(this),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, FocusFlowApp.CHANNEL_SESSION)
            .setSmallIcon(android.R.drawable.ic_media_pause)
            .setContentTitle("FocusFlow")
            .setContentText(contentText)
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_delete, "Stop", stopIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timerJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }
}
