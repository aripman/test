package com.focusflow.android.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DigitalClockComposable(
    timeMs: Long,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()) }
    val date = remember(timeMs) { Date(timeMs) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeFormat.format(date),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = dateFormat.format(date),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
