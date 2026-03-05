package com.focusflow.android.presentation.blocker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.repository.SessionRepository
import com.focusflow.android.domain.usecase.session.EndFocusSessionUseCase
import com.focusflow.android.domain.usecase.session.GetActiveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockerViewModel @Inject constructor(
    private val getActiveSession: GetActiveSessionUseCase,
    private val endSession: EndFocusSessionUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    enum class EscapePhase { LOCKED, BREATHING, HOLD_TO_ESCAPE, ESCAPED }

    data class BlockerUiState(
        val blockedAppName: String = "",
        val sessionMode: String = "",
        val remainingMs: Long = 0L,
        val escapePhase: EscapePhase = EscapePhase.LOCKED,
        val breathingProgress: Float = 0f,
        val escapeHoldProgress: Float = 0f
    )

    private val _uiState = MutableStateFlow(BlockerUiState())
    val uiState: StateFlow<BlockerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getActiveSession().collect { state ->
                if (state is SessionState.Active) {
                    _uiState.update {
                        it.copy(
                            sessionMode = state.mode.name,
                            remainingMs = state.remainingMs
                        )
                    }
                }
            }
        }
    }

    fun setBlockedApp(appName: String) {
        _uiState.update { it.copy(blockedAppName = appName) }
    }

    fun startEscapeSequence() {
        _uiState.update { it.copy(escapePhase = EscapePhase.BREATHING) }
        viewModelScope.launch {
            val totalMs = 30_000L
            val startTime = System.currentTimeMillis()
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
                _uiState.update { it.copy(breathingProgress = progress) }
                if (elapsed >= totalMs) break
                delay(50L)
            }
            _uiState.update { it.copy(escapePhase = EscapePhase.HOLD_TO_ESCAPE) }
        }
    }

    fun onHoldProgress(progress: Float) {
        _uiState.update { it.copy(escapeHoldProgress = progress) }
        if (progress >= 1f) {
            viewModelScope.launch {
                val state = getActiveSession().first()
                if (state is SessionState.Active) {
                    sessionRepository.incrementEscapeAttempts(state.session.id)
                    endSession(state.session.id, wasCompleted = false)
                }
                _uiState.update { it.copy(escapePhase = EscapePhase.ESCAPED) }
            }
        }
    }

    fun cancelEscape() {
        _uiState.update {
            it.copy(
                escapePhase = EscapePhase.LOCKED,
                breathingProgress = 0f,
                escapeHoldProgress = 0f
            )
        }
    }
}
