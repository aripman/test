package com.focusflow.android.presentation.session

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusflow.android.core.util.TimeFormatter
import com.focusflow.android.domain.model.SessionState

@Composable
fun SessionScreen(
    onSessionEnd: () -> Unit,
    onManageWhitelist: (Long) -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sessionState) {
        if (uiState.sessionState is SessionState.Idle) onSessionEnd()
    }

    if (uiState.showEndConfirmation) {
        AlertDialog(
            onDismissRequest = viewModel::onStopDismissed,
            title = { Text("End session?") },
            text = { Text("This session won't count as completed.") },
            confirmButton = {
                TextButton(onClick = viewModel::onStopConfirmed) { Text("End", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onStopDismissed) { Text("Keep going") }
            }
        )
    }

    val activeState = uiState.sessionState as? SessionState.Active

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (activeState != null) {
                Text(
                    text = activeState.mode.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(48.dp))

                // Circular countdown
                CountdownTimer(
                    remainingMs = activeState.remainingMs,
                    totalMs = activeState.session.plannedDurationMs,
                    isPaused = activeState.isPaused
                )

                Spacer(Modifier.height(48.dp))

                // Controls
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = viewModel::onPauseToggle,
                        modifier = Modifier.size(56.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = if (activeState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (activeState.isPaused) "Resume" else "Pause"
                        )
                    }
                    FilledTonalButton(
                        onClick = viewModel::onStopRequested,
                        modifier = Modifier.size(56.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (uiState.whitelistedApps.isNotEmpty()) {
                    Spacer(Modifier.height(32.dp))
                    Text(
                        "Allowed apps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    uiState.whitelistedApps.forEach { app ->
                        Text(app.appName, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(16.dp))
                TextButton(onClick = { onManageWhitelist(activeState.mode.id) }) {
                    Text("Manage allowed apps")
                }
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun CountdownTimer(remainingMs: Long, totalMs: Long, isPaused: Boolean) {
    val progress = if (totalMs > 0) remainingMs.toFloat() / totalMs.toFloat() else 0f

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 6.dp,
            color = if (isPaused) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = TimeFormatter.formatMs(remainingMs),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            if (isPaused) {
                Text(
                    "Paused",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
