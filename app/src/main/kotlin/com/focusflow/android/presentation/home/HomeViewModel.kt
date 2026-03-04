package com.focusflow.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.android.domain.model.CalendarEvent
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.ModeType
import com.focusflow.android.domain.model.SessionState
import com.focusflow.android.domain.usecase.calendar.GetUpcomingEventsUseCase
import com.focusflow.android.domain.usecase.mode.GetFocusModesUseCase
import com.focusflow.android.domain.usecase.mode.SaveFocusModeUseCase
import com.focusflow.android.domain.usecase.session.GetActiveSessionUseCase
import com.focusflow.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFocusModes: GetFocusModesUseCase,
    private val saveFocusMode: SaveFocusModeUseCase,
    private val getActiveSession: GetActiveSessionUseCase,
    private val getUpcomingEvents: GetUpcomingEventsUseCase,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    data class HomeUiState(
        val currentTimeMs: Long = System.currentTimeMillis(),
        val sessionState: SessionState = SessionState.Idle,
        val todayFocusTimeMs: Long = 0L,
        val todaySessionCount: Int = 0,
        val focusModes: List<FocusMode> = emptyList(),
        val upcomingEvents: List<CalendarEvent> = emptyList(),
        val selectedMode: FocusMode? = null,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        startClock()
        observeSession()
        loadModes()
        loadStats()
        loadEvents()
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                _uiState.update { it.copy(currentTimeMs = System.currentTimeMillis()) }
                delay(1_000L)
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            getActiveSession().collect { state ->
                _uiState.update { it.copy(sessionState = state) }
            }
        }
    }

    private fun loadModes() {
        viewModelScope.launch {
            getFocusModes().collect { modes ->
                _uiState.update { state ->
                    state.copy(
                        focusModes = modes,
                        selectedMode = state.selectedMode ?: modes.find { it.isDefault } ?: modes.firstOrNull(),
                        isLoading = false
                    )
                }
                // Seed default modes if none exist
                if (modes.isEmpty()) seedDefaultModes()
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            sessionRepository.getTodayFocusTimeMs().collect { ms ->
                _uiState.update { it.copy(todayFocusTimeMs = ms) }
            }
        }
        viewModelScope.launch {
            sessionRepository.getTodaySessionCount().collect { count ->
                _uiState.update { it.copy(todaySessionCount = count) }
            }
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            try {
                val events = getUpcomingEvents()
                _uiState.update { it.copy(upcomingEvents = events.take(3)) }
            } catch (e: Exception) { /* permission not granted */ }
        }
    }

    private fun seedDefaultModes() {
        viewModelScope.launch {
            listOf(
                FocusMode(name = "Deep Work", type = ModeType.WORK, durationMinutes = 90,
                    iconName = "work", colorSeed = 0xFF1B5E20, isDefault = true,
                    allowedApps = emptyList()),
                FocusMode(name = "Study", type = ModeType.STUDY, durationMinutes = 50,
                    iconName = "school", colorSeed = 0xFF0D47A1, isDefault = false,
                    allowedApps = emptyList()),
                FocusMode(name = "Wind Down", type = ModeType.SLEEP, durationMinutes = 30,
                    iconName = "bedtime", colorSeed = 0xFF4A148C, isDefault = false,
                    allowedApps = emptyList())
            ).forEach { saveFocusMode(it) }
        }
    }

    fun selectMode(mode: FocusMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }
}
