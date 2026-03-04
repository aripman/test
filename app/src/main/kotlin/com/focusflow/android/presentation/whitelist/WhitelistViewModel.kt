package com.focusflow.android.presentation.whitelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.android.domain.model.AppInfo
import com.focusflow.android.domain.repository.AppRepository
import com.focusflow.android.domain.usecase.apps.GetInstalledAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WhitelistViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getInstalledApps: GetInstalledAppsUseCase,
    private val appRepository: AppRepository
) : ViewModel() {

    private val modeId: Long = checkNotNull(savedStateHandle["modeId"])

    data class WhitelistUiState(
        val allApps: List<AppInfo> = emptyList(),
        val whitelistedPackages: Set<String> = emptySet(),
        val searchQuery: String = "",
        val isLoading: Boolean = true
    ) {
        val filteredApps = allApps.filter { app ->
            searchQuery.isBlank() || app.appName.contains(searchQuery, ignoreCase = true)
        }
    }

    private val _uiState = MutableStateFlow(WhitelistUiState())
    val uiState: StateFlow<WhitelistUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val apps = getInstalledApps()
            _uiState.update { it.copy(allApps = apps, isLoading = false) }
        }
        viewModelScope.launch {
            appRepository.getWhitelistedApps(modeId).collect { whitelisted ->
                _uiState.update { it.copy(whitelistedPackages = whitelisted.map { a -> a.packageName }.toSet()) }
            }
        }
    }

    fun onSearchChanged(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun toggleApp(app: AppInfo) {
        viewModelScope.launch {
            if (app.packageName in _uiState.value.whitelistedPackages) {
                appRepository.removeAppFromWhitelist(modeId, app.packageName)
            } else {
                appRepository.addAppToWhitelist(modeId, app.packageName, app.appName)
            }
        }
    }
}
