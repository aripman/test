package com.focusflow.android.data.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.focusflow.android.data.datastore.SessionStateDataStore
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class FocusAccessibilityService : AccessibilityService() {

    @Inject lateinit var sessionStateDataStore: SessionStateDataStore
    @Inject lateinit var appRepository: AppRepository
    @Inject lateinit var blockerOverlayManager: BlockerOverlayManager

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentActiveState: SessionState.Active? = null
    private var lastCheckedPackage: String = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        observeSessionState()
    }

    private fun observeSessionState() {
        serviceScope.launch {
            sessionStateDataStore.sessionStateFlow.collect { state ->
                currentActiveState = state as? SessionState.Active
                if (state is SessionState.Idle) {
                    blockerOverlayManager.dismissBlocker()
                }
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val packageName = event.packageName?.toString() ?: return
        val activeState = currentActiveState ?: return

        if (packageName == lastCheckedPackage) return
        lastCheckedPackage = packageName

        // Never block our own app or system UI
        if (packageName == applicationContext.packageName) {
            blockerOverlayManager.dismissBlocker()
            return
        }
        if (packageName == "com.android.systemui" || packageName == "android") return

        serviceScope.launch {
            val isBlocked = appRepository.isAppBlocked(packageName, activeState.mode.id)
            if (isBlocked) {
                blockerOverlayManager.showBlocker(
                    blockedPackage = packageName,
                    blockedAppName = getAppName(packageName)
                )
            } else {
                blockerOverlayManager.dismissBlocker()
            }
        }
    }

    private fun getAppName(packageName: String): String = try {
        applicationContext.packageManager
            .getApplicationLabel(
                applicationContext.packageManager.getApplicationInfo(packageName, 0)
            ).toString()
    } catch (e: Exception) { packageName }

    override fun onInterrupt() {
        blockerOverlayManager.dismissBlocker()
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
