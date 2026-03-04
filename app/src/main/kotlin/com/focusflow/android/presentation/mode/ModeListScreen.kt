package com.focusflow.android.presentation.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeListScreen(
    onCreateMode: () -> Unit,
    onEditMode: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: ModeViewModel = hiltViewModel()
) {
    val modes by viewModel.modes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Focus Modes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateMode,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Mode") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(modes, key = { it.id }) { mode ->
                ListItem(
                    headlineContent = { Text(mode.name) },
                    supportingContent = {
                        Text("${mode.durationMinutes} min · ${mode.type.name.lowercase().replaceFirstChar { it.uppercase() }}")
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onEditMode(mode.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { viewModel.delete(mode) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }
}
