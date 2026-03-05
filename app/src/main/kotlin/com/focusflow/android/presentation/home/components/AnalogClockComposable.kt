package com.focusflow.android.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockComposable(
    timeMs: Long,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }

    val seconds = remember(timeMs) {
        calendar.timeInMillis = timeMs
        calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f
    }
    val minutes = remember(timeMs) {
        calendar.get(Calendar.MINUTE) + seconds / 60f
    }
    val hours = remember(timeMs) {
        (calendar.get(Calendar.HOUR) % 12) + minutes / 60f
    }

    val secondAngle by animateFloatAsState(
        targetValue = seconds * 6f,
        animationSpec = tween(durationMillis = 250, easing = LinearEasing),
        label = "second_hand"
    )

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.92f

        // Face
        drawCircle(color = surfaceVariant, radius = radius, center = center)
        drawCircle(
            color = outline.copy(alpha = 0.25f),
            radius = radius,
            center = center,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Hour markers
        for (i in 0 until 60) {
            val angle = Math.toRadians((i * 6 - 90).toDouble())
            val isHour = i % 5 == 0
            val length = if (isHour) radius * 0.1f else radius * 0.05f
            val width = if (isHour) 2.5.dp.toPx() else 1.dp.toPx()
            val outerR = radius * 0.94f
            val innerR = outerR - length
            drawLine(
                color = if (isHour) onSurface else onSurfaceVariant.copy(alpha = 0.5f),
                start = Offset(center.x + outerR * cos(angle).toFloat(), center.y + outerR * sin(angle).toFloat()),
                end = Offset(center.x + innerR * cos(angle).toFloat(), center.y + innerR * sin(angle).toFloat()),
                strokeWidth = width,
                cap = StrokeCap.Round
            )
        }

        // Hour hand
        drawHand(center, hours * 30f - 90f, radius * 0.48f, onSurface, 6.dp.toPx())
        // Minute hand
        drawHand(center, minutes * 6f - 90f, radius * 0.68f, onSurface, 4.dp.toPx())
        // Second hand
        drawHand(center, secondAngle - 90f, radius * 0.78f, primary, 2.dp.toPx())
        // Counter-tail for second hand
        drawHand(center, secondAngle + 90f, radius * 0.15f, primary, 2.dp.toPx())

        // Center dot
        drawCircle(color = primary, radius = 5.dp.toPx(), center = center)
        drawCircle(color = surfaceVariant, radius = 2.dp.toPx(), center = center)
    }
}

private fun DrawScope.drawHand(
    center: Offset,
    angleDeg: Float,
    length: Float,
    color: Color,
    strokeWidth: Float
) {
    val rad = Math.toRadians(angleDeg.toDouble())
    drawLine(
        color = color,
        start = center,
        end = Offset(center.x + length * cos(rad).toFloat(), center.y + length * sin(rad).toFloat()),
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}
