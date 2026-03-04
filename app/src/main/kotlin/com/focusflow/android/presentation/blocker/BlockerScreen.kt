package com.focusflow.android.presentation.blocker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import com.focusflow.android.presentation.blocker.components.BreathingExercise
import com.focusflow.android.presentation.blocker.components.HoldToEscape

@Composable
fun BlockerScreen(
    blockedAppName: String,
    onEscaped: () -> Unit,
    viewModel: BlockerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(blockedAppName) {
        viewModel.setBlockedApp(blockedAppName)
    }

    LaunchedEffect(uiState.escapePhase) {
        if (uiState.escapePhase == BlockerViewModel.EscapePhase.ESCAPED) {
            onEscaped()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        when (uiState.escapePhase) {
            BlockerViewModel.EscapePhase.LOCKED -> {
                LockedContent(
                    blockedAppName = uiState.blockedAppName,
                    modeName = uiState.sessionMode,
                    remainingMs = uiState.remainingMs,
                    onRequestEscape = viewModel::startEscapeSequence
                )
            }
            BlockerViewModel.EscapePhase.BREATHING -> {
                BreathingExercise(
                    progress = uiState.breathingProgress,
                    onCancel = viewModel::cancelEscape
                )
            }
            BlockerViewModel.EscapePhase.HOLD_TO_ESCAPE -> {
                HoldToEscape(onProgressChanged = viewModel::onHoldProgress)
            }
            BlockerViewModel.EscapePhase.ESCAPED -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun LockedContent(
    blockedAppName: String,
    modeName: String,
    remainingMs: Long,
    onRequestEscape: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = blockedAppName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (modeName.isNotBlank()) "is blocked during your $modeName session"
                   else "is blocked during your focus session",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        if (remainingMs > 0) {
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        text = "${TimeFormatter.formatMs(remainingMs)} remaining",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
        Spacer(Modifier.height(48.dp))
        FilledTonalButton(onClick = onRequestEscape) {
            Text("Emergency Exit")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Requires a 30-second breathing exercise",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
