package com.focusflow.android.presentation.whitelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusflow.android.domain.model.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhitelistScreen(
    onDone: () -> Unit,
    viewModel: WhitelistViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Allowed Apps") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchChanged,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search apps…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.filteredApps, key = { it.packageName }) { app ->
                    AppListItem(
                        app = app,
                        isWhitelisted = app.packageName in uiState.whitelistedPackages,
                        onToggle = { viewModel.toggleApp(app) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppListItem(app: AppInfo, isWhitelisted: Boolean, onToggle: () -> Unit) {
    ListItem(
        headlineContent = { Text(app.appName) },
        supportingContent = { Text(app.packageName, style = MaterialTheme.typography.labelSmall) },
        trailingContent = {
            Switch(checked = isWhitelisted, onCheckedChange = { onToggle() })
        }
    )
    HorizontalDivider(thickness = 0.5.dp)
}
