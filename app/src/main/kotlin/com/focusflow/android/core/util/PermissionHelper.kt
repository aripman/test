package com.focusflow.android.core.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Process
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

object PermissionHelper {

    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun hasOverlayPermission(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    fun hasAccessibilityPermission(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = am.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        )
        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == context.packageName
        }
    }

    fun openUsageStatsSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    fun openOverlaySettings(context: Context) {
        context.startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        )
    }

    fun openAccessibilitySettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }
}
