package com.focusflow.android.presentation.blocker.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HoldToEscape(
    onProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var isHolding by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            val totalMs = 3_000L
            val startTime = System.currentTimeMillis()
            while (isHolding) {
                val elapsed = System.currentTimeMillis() - startTime
                progress = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
                onProgressChanged(progress)
                if (progress >= 1f) break
                delay(16L)
            }
            if (!isHolding) {
                progress = 0f
                onProgressChanged(0f)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Emergency Exit",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Hold the button for 3 seconds to leave your focus session early",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.error,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .size(140.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isHolding = true
                                tryAwaitRelease()
                                isHolding = false
                            }
                        )
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isHolding) MaterialTheme.colorScheme.error
                                     else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = if (isHolding) "Keep holding…" else "Hold to Exit",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isHolding) MaterialTheme.colorScheme.onError
                            else MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
