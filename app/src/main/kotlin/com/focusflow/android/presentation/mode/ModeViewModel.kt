package com.focusflow.android.presentation.mode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.ModeType
import com.focusflow.android.domain.usecase.mode.DeleteFocusModeUseCase
import com.focusflow.android.domain.usecase.mode.GetFocusModesUseCase
import com.focusflow.android.domain.usecase.mode.SaveFocusModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFocusModes: GetFocusModesUseCase,
    private val saveMode: SaveFocusModeUseCase,
    private val deleteMode: DeleteFocusModeUseCase
) : ViewModel() {

    data class ModeEditorState(
        val id: Long = 0,
        val name: String = "",
        val type: ModeType = ModeType.CUSTOM,
        val durationMinutes: Int = 25,
        val isDefault: Boolean = false,
        val isSaving: Boolean = false
    )

    val modes: StateFlow<List<FocusMode>> = getFocusModes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _editorState = MutableStateFlow(ModeEditorState())
    val editorState: StateFlow<ModeEditorState> = _editorState.asStateFlow()

    init {
        val modeId = savedStateHandle.get<Long>("modeId") ?: -1L
        if (modeId != -1L) {
            viewModelScope.launch {
                getFocusModes().first().find { it.id == modeId }?.let { mode ->
                    _editorState.update {
                        it.copy(
                            id = mode.id,
                            name = mode.name,
                            type = mode.type,
                            durationMinutes = mode.durationMinutes,
                            isDefault = mode.isDefault
                        )
                    }
                }
            }
        }
    }

    fun onNameChanged(name: String) = _editorState.update { it.copy(name = name) }
    fun onTypeChanged(type: ModeType) = _editorState.update { it.copy(type = type) }
    fun onDurationChanged(minutes: Int) = _editorState.update { it.copy(durationMinutes = minutes) }
    fun onDefaultChanged(isDefault: Boolean) = _editorState.update { it.copy(isDefault = isDefault) }

    fun save(onDone: () -> Unit) {
        val state = _editorState.value
        if (state.name.isBlank()) return
        viewModelScope.launch {
            _editorState.update { it.copy(isSaving = true) }
            saveMode(
                FocusMode(
                    id = state.id,
                    name = state.name.trim(),
                    type = state.type,
                    durationMinutes = state.durationMinutes,
                    iconName = state.type.name.lowercase(),
                    colorSeed = 0L,
                    isDefault = state.isDefault
                )
            )
            onDone()
        }
    }

    fun delete(mode: FocusMode) {
        viewModelScope.launch { deleteMode(mode) }
    }
}
