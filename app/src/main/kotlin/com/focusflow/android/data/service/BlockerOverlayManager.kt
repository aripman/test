package com.focusflow.android.data.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.*
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockerOverlayManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
    }

    fun showBlocker(
        blockedPackage: String,
        blockedAppName: String,
        onEscaped: () -> Unit = {}
    ) {
        if (overlayView != null) return
        if (!Settings.canDrawOverlays(context)) return

        mainHandler.post {
            val container = FrameLayout(context)

            // Set up a minimal lifecycle for the ComposeView
            val lifecycleOwner = object : LifecycleOwner {
                val registry = LifecycleRegistry(this)
                override val lifecycle: Lifecycle get() = registry
            }
            lifecycleOwner.registry.currentState = Lifecycle.State.RESUMED

            val composeView = ComposeView(context).apply {
                setViewTreeLifecycleOwner(lifecycleOwner)
                setViewTreeSavedStateRegistryOwner(null)
                setContent {
                    com.focusflow.android.core.ui.theme.FocusFlowTheme {
                        com.focusflow.android.presentation.blocker.BlockerScreen(
                            blockedAppName = blockedAppName,
                            onEscaped = {
                                dismissBlocker()
                                onEscaped()
                            }
                        )
                    }
                }
            }

            container.addView(composeView)
            overlayView = container
            try {
                windowManager.addView(container, layoutParams)
            } catch (e: Exception) {
                overlayView = null
            }
        }
    }

    fun dismissBlocker() {
        mainHandler.post {
            overlayView?.let { view ->
                try { windowManager.removeView(view) } catch (e: Exception) { /* ignore */ }
            }
            overlayView = null
        }
    }

    fun isShowing() = overlayView != null
}
