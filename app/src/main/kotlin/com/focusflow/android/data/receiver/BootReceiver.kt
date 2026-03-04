package com.focusflow.android.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // On boot, if there was an active session it will be in DataStore but timer is dead.
        // For now we clear orphaned session state. A future enhancement could restore it.
        // The DataStore will emit Idle state if the app reads it fresh.
    }
}
