package com.focusflow.android.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.focusflow.android.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session_state"
)

@Singleton
class SessionStateDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_IS_ACTIVE = booleanPreferencesKey("is_active")
        val KEY_SESSION_ID = longPreferencesKey("session_id")
        val KEY_MODE_ID = longPreferencesKey("mode_id")
        val KEY_MODE_NAME = stringPreferencesKey("mode_name")
        val KEY_START_TIME = longPreferencesKey("start_time")
        val KEY_PLANNED_DURATION = longPreferencesKey("planned_duration_ms")
        val KEY_REMAINING_MS = longPreferencesKey("remaining_ms")
        val KEY_IS_PAUSED = booleanPreferencesKey("is_paused")
        val KEY_ALLOWED_APPS = stringPreferencesKey("allowed_apps_json")
        val KEY_MODE_TYPE = stringPreferencesKey("mode_type")
    }

    val sessionStateFlow: Flow<SessionState> = context.sessionDataStore.data
        .map { prefs -> prefs.toSessionState() }
        .distinctUntilChanged()

    suspend fun setActiveSession(sessionId: Long, mode: FocusMode) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_IS_ACTIVE] = true
            prefs[KEY_SESSION_ID] = sessionId
            prefs[KEY_MODE_ID] = mode.id
            prefs[KEY_MODE_NAME] = mode.name
            prefs[KEY_MODE_TYPE] = mode.type.name
            prefs[KEY_START_TIME] = System.currentTimeMillis()
            prefs[KEY_PLANNED_DURATION] = mode.durationMinutes * 60_000L
            prefs[KEY_REMAINING_MS] = mode.durationMinutes * 60_000L
            prefs[KEY_IS_PAUSED] = false
            prefs[KEY_ALLOWED_APPS] = Json.encodeToString(mode.allowedApps)
        }
    }

    suspend fun updateRemainingTime(remainingMs: Long) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_REMAINING_MS] = remainingMs
        }
    }

    suspend fun setPaused(paused: Boolean) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_IS_PAUSED] = paused
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs -> prefs.clear() }
    }

    private fun Preferences.toSessionState(): SessionState {
        val isActive = this[KEY_IS_ACTIVE] ?: false
        if (!isActive) return SessionState.Idle

        val plannedDuration = this[KEY_PLANNED_DURATION] ?: 0L
        val remainingMs = this[KEY_REMAINING_MS] ?: 0L

        return SessionState.Active(
            session = FocusSession(
                id = this[KEY_SESSION_ID] ?: 0L,
                modeId = this[KEY_MODE_ID] ?: 0L,
                modeName = this[KEY_MODE_NAME] ?: "",
                startTime = this[KEY_START_TIME] ?: 0L,
                plannedDurationMs = plannedDuration
            ),
            mode = FocusMode(
                id = this[KEY_MODE_ID] ?: 0L,
                name = this[KEY_MODE_NAME] ?: "",
                type = try { ModeType.valueOf(this[KEY_MODE_TYPE] ?: "CUSTOM") } catch (e: Exception) { ModeType.CUSTOM },
                durationMinutes = (plannedDuration / 60_000).toInt(),
                iconName = "",
                colorSeed = 0L,
                allowedApps = try { Json.decodeFromString(this[KEY_ALLOWED_APPS] ?: "[]") } catch (e: Exception) { emptyList() }
            ),
            remainingMs = remainingMs,
            elapsedMs = plannedDuration - remainingMs,
            isPaused = this[KEY_IS_PAUSED] ?: false
        )
    }
}
