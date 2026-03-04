package com.focusflow.android.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isSystemApp: Boolean = false,
    val isWhitelisted: Boolean = false
)
