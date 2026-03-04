package com.focusflow.android.presentation.mode

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.focusflow.android.domain.model.ModeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeEditorScreen(
    onDone: () -> Unit,
    viewModel: ModeViewModel = hiltViewModel()
) {
    val state by viewModel.editorState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.id == 0L) "New Mode" else "Edit Mode") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = { Text("Mode name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Text("Type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeType.values().forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { viewModel.onTypeChanged(type) },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Text("Duration: ${state.durationMinutes} minutes", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = state.durationMinutes.toFloat(),
                onValueChange = { viewModel.onDurationChanged(it.toInt()) },
                valueRange = 5f..180f,
                steps = 34,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(25, 50, 90).forEach { mins ->
                    FilterChip(
                        selected = state.durationMinutes == mins,
                        onClick = { viewModel.onDurationChanged(mins) },
                        label = { Text("${mins}m") }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Set as default mode", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = state.isDefault, onCheckedChange = viewModel::onDefaultChanged)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.save(onDone) },
                enabled = state.name.isNotBlank() && !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (state.isSaving) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                else Text("Save Mode")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
