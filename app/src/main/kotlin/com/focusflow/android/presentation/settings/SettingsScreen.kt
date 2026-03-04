package com.focusflow.android.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.focusflow.android.core.util.PermissionHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var refreshTick by remember { mutableIntStateOf(0) }

    val hasAccessibility = remember(refreshTick) { PermissionHelper.hasAccessibilityPermission(context) }
    val hasOverlay = remember(refreshTick) { PermissionHelper.hasOverlayPermission(context) }
    val hasUsageStats = remember(refreshTick) { PermissionHelper.hasUsageStatsPermission(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Required Permissions", style = MaterialTheme.typography.titleMedium)
            Text(
                "These permissions are required for FocusFlow to block apps during sessions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))

            PermissionCard(
                title = "Accessibility Service",
                description = "Detects which app is in the foreground so FocusFlow can block non-whitelisted apps.",
                isGranted = hasAccessibility,
                onGrant = {
                    PermissionHelper.openAccessibilitySettings(context)
                    refreshTick++
                }
            )
            PermissionCard(
                title = "Display Over Other Apps",
                description = "Allows FocusFlow to show the blocking screen on top of other apps.",
                isGranted = hasOverlay,
                onGrant = {
                    PermissionHelper.openOverlaySettings(context)
                    refreshTick++
                }
            )
            PermissionCard(
                title = "Usage Access",
                description = "Reads app usage data to track focus statistics and detect foreground apps.",
                isGranted = hasUsageStats,
                onGrant = {
                    PermissionHelper.openUsageStatsSettings(context)
                    refreshTick++
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("About", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("FocusFlow") },
                supportingContent = { Text("Version 1.0.0") }
            )
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrant: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(title, style = MaterialTheme.typography.titleSmall)
            }
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (!isGranted) {
                FilledTonalButton(onClick = onGrant, modifier = Modifier.align(Alignment.End)) {
                    Text("Grant")
                }
            }
        }
    }
}
