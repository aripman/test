package com.focusflow.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.android.domain.model.AppInfo
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.usecase.apps.GetWhitelistedAppsUseCase
import com.focusflow.android.domain.usecase.session.EndFocusSessionUseCase
import com.focusflow.android.domain.usecase.session.GetActiveSessionUseCase
import com.focusflow.android.domain.usecase.session.PauseSessionUseCase
import com.focusflow.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val getActiveSession: GetActiveSessionUseCase,
    private val endSession: EndFocusSessionUseCase,
    private val pauseSession: PauseSessionUseCase,
    private val getWhitelistedApps: GetWhitelistedAppsUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    data class SessionUiState(
        val sessionState: SessionState = SessionState.Idle,
        val whitelistedApps: List<AppInfo> = emptyList(),
        val showEndConfirmation: Boolean = false
    )

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getActiveSession().collect { state ->
                _uiState.update { it.copy(sessionState = state) }
                if (state is SessionState.Active) {
                    loadWhitelistedApps(state.mode.id)
                }
            }
        }
    }

    private fun loadWhitelistedApps(modeId: Long) {
        viewModelScope.launch {
            getWhitelistedApps(modeId).collect { apps ->
                _uiState.update { it.copy(whitelistedApps = apps) }
            }
        }
    }

    fun onPauseToggle() {
        viewModelScope.launch {
            val state = _uiState.value.sessionState
            if (state is SessionState.Active) {
                if (state.isPaused) sessionRepository.resumeSession()
                else pauseSession()
            }
        }
    }

    fun onStopRequested() = _uiState.update { it.copy(showEndConfirmation = true) }
    fun onStopDismissed() = _uiState.update { it.copy(showEndConfirmation = false) }

    fun onStopConfirmed() {
        viewModelScope.launch {
            val state = _uiState.value.sessionState
            if (state is SessionState.Active) {
                endSession(state.session.id, wasCompleted = false)
            }
        }
    }
}
