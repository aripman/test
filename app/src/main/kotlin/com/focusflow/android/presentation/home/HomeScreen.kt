package com.focusflow.android.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusflow.android.core.util.TimeFormatter
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.presentation.home.components.AnalogClockComposable
import com.focusflow.android.presentation.home.components.DigitalClockComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStartSession: (FocusMode) -> Unit,
    onManageModes: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCalendar: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FocusFlow", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Light) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            AnalogClockComposable(
                timeMs = uiState.currentTimeMs,
                modifier = Modifier.size(260.dp)
            )

            Spacer(Modifier.height(12.dp))

            DigitalClockComposable(
                timeMs = uiState.currentTimeMs,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Today",
                    value = TimeFormatter.formatMsVerbose(uiState.todayFocusTimeMs),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Sessions",
                    value = uiState.todaySessionCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Mode selector
            SectionHeader(
                title = "Focus Mode",
                actionLabel = "Manage",
                onAction = onManageModes
            )
            Spacer(Modifier.height(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                uiState.focusModes.forEach { mode ->
                    ModeChip(
                        mode = mode,
                        selected = uiState.selectedMode?.id == mode.id,
                        onClick = { viewModel.selectMode(mode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Start button
            val activeSession = uiState.sessionState
            if (activeSession is SessionState.Active) {
                FilledTonalButton(
                    onClick = { /* navigate to session */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp)
                ) {
                    Text("Return to Session")
                }
            } else {
                Button(
                    onClick = { uiState.selectedMode?.let { onStartSession(it) } },
                    enabled = uiState.selectedMode != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp)
                ) {
                    Text(
                        text = uiState.selectedMode?.let {
                            "Start ${it.name} (${it.durationMinutes}m)"
                        } ?: "Select a mode",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // Calendar events
            if (uiState.upcomingEvents.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                SectionHeader(
                    title = "Upcoming",
                    actionLabel = "View all",
                    onAction = onOpenCalendar
                )
                Spacer(Modifier.height(8.dp))
                uiState.upcomingEvents.forEach { event ->
                    ListItem(
                        headlineContent = { Text(event.title) },
                        supportingContent = {
                            Text(java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(event.startTime)))
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Light)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        TextButton(onClick = onAction) { Text(actionLabel) }
    }
}

@Composable
private fun ModeChip(
    mode: FocusMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (selected) FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) else FilterChipDefaults.filterChipColors()

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text("${mode.name}  ·  ${mode.durationMinutes}m") },
        colors = colors,
        modifier = modifier
    )
}
