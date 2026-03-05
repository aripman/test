package com.focusflow.android.presentation.blocker.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BreathingExercise(
    progress: Float,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phase = when {
        progress < 0.4f -> "Breathe In"
        progress < 0.7f -> "Hold"
        else -> "Breathe Out"
    }
    val phaseProgress = when {
        progress < 0.4f -> progress / 0.4f
        progress < 0.7f -> (progress - 0.4f) / 0.3f
        else -> (progress - 0.7f) / 0.3f
    }

    val scale by animateFloatAsState(
        targetValue = when {
            progress < 0.4f -> 0.5f + phaseProgress * 0.5f
            progress < 0.7f -> 1.0f
            else -> 1.0f - phaseProgress * 0.5f
        },
        animationSpec = tween(durationMillis = 150),
        label = "breathe_scale"
    )

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Take a moment to breathe",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Complete the breathing exercise to unlock emergency exit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Box(
                modifier = Modifier
                    .size((80 * scale + 20).dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            )
            Text(
                text = phase,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(Modifier.height(48.dp))

        TextButton(onClick = onCancel) {
            Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
