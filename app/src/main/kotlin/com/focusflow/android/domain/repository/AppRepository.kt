package com.focusflow.android.domain.repository

import com.focusflow.android.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    fun getWhitelistedApps(modeId: Long): Flow<List<AppInfo>>
    suspend fun addAppToWhitelist(modeId: Long, packageName: String, appName: String)
    suspend fun removeAppFromWhitelist(modeId: Long, packageName: String)
    suspend fun setWhitelist(modeId: Long, packageNames: List<String>)
    suspend fun isAppBlocked(packageName: String, activeModeId: Long): Boolean
}
