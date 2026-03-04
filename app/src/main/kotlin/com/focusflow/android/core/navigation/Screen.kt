package com.focusflow.android.core.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Session : Screen("session")
    object WhitelistManager : Screen("whitelist/{modeId}") {
        fun createRoute(modeId: Long) = "whitelist/$modeId"
    }
    object ModeList : Screen("modes")
    object ModeEditor : Screen("mode_editor?modeId={modeId}") {
        fun createRoute(modeId: Long? = null) =
            if (modeId != null) "mode_editor?modeId=$modeId" else "mode_editor"
    }
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
}
