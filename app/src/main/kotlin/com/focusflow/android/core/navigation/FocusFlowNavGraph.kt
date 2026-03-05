package com.focusflow.android.core.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.focusflow.android.data.service.FocusSessionService
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.presentation.home.HomeScreen
import com.focusflow.android.presentation.mode.ModeEditorScreen
import com.focusflow.android.presentation.mode.ModeListScreen
import com.focusflow.android.presentation.session.SessionScreen
import com.focusflow.android.presentation.settings.SettingsScreen
import com.focusflow.android.presentation.whitelist.WhitelistScreen

@Composable
fun FocusFlowNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(280))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(280))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(280))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(280))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartSession = { mode ->
                    startSession(context, mode)
                    navController.navigate(Screen.Session.route)
                },
                onManageModes = { navController.navigate(Screen.ModeList.route) },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenCalendar = { /* calendar screen */ }
            )
        }

        composable(Screen.Session.route) {
            SessionScreen(
                onSessionEnd = { navController.popBackStack(Screen.Home.route, inclusive = false) },
                onManageWhitelist = { modeId ->
                    navController.navigate(Screen.WhitelistManager.createRoute(modeId))
                }
            )
        }

        composable(
            route = Screen.WhitelistManager.route,
            arguments = listOf(navArgument("modeId") { type = NavType.LongType })
        ) {
            WhitelistScreen(onDone = { navController.popBackStack() })
        }

        composable(Screen.ModeList.route) {
            ModeListScreen(
                onCreateMode = { navController.navigate(Screen.ModeEditor.createRoute()) },
                onEditMode = { modeId -> navController.navigate(Screen.ModeEditor.createRoute(modeId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ModeEditor.route,
            arguments = listOf(navArgument("modeId") { type = NavType.LongType; defaultValue = -1L })
        ) {
            ModeEditorScreen(onDone = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

private fun startSession(context: Context, mode: FocusMode) {
    context.startForegroundService(FocusSessionService.startIntent(context))
}
