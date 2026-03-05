package com.focusflow.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SESSION,
                "Focus Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active focus session timer"
                setShowBadge(false)
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ALERTS,
                "Session Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Session start and completion alerts"
            }
        )
    }

    companion object {
        const val CHANNEL_SESSION = "focus_session_channel"
        const val CHANNEL_ALERTS = "focus_alerts_channel"
    }
}
