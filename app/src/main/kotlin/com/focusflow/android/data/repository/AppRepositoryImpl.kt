package com.focusflow.android.data.repository

import android.content.pm.PackageManager
import com.focusflow.android.data.local.dao.AppEntryDao
import com.focusflow.android.data.local.entity.AppEntryEntity
import com.focusflow.android.domain.model.AppInfo
import com.focusflow.android.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dao: AppEntryDao,
    private val packageManager: PackageManager
) : AppRepository {

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                AppInfo(
                    packageName = pkg,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager),
                    isSystemApp = (resolveInfo.activityInfo.applicationInfo.flags and
                        android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .sortedBy { it.appName }
    }

    override fun getWhitelistedApps(modeId: Long): Flow<List<AppInfo>> =
        dao.getAppsForMode(modeId).map { entries ->
            entries.map { entry ->
                AppInfo(
                    packageName = entry.packageName,
                    appName = entry.appName,
                    icon = try { packageManager.getApplicationIcon(entry.packageName) } catch (e: Exception) { null },
                    isWhitelisted = true
                )
            }
        }

    override suspend fun addAppToWhitelist(modeId: Long, packageName: String, appName: String) =
        dao.insertApp(AppEntryEntity(modeId = modeId, packageName = packageName, appName = appName))

    override suspend fun removeAppFromWhitelist(modeId: Long, packageName: String) =
        dao.removeApp(modeId, packageName)

    override suspend fun setWhitelist(modeId: Long, packageNames: List<String>) {
        val apps = packageNames.map { pkg ->
            val name = try {
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(pkg, 0)
                ).toString()
            } catch (e: Exception) { pkg }
            AppEntryEntity(modeId = modeId, packageName = pkg, appName = name)
        }
        dao.replaceAppsForMode(modeId, apps)
    }

    override suspend fun isAppBlocked(packageName: String, activeModeId: Long): Boolean {
        val whitelisted = dao.getPackageNamesForMode(activeModeId)
        return packageName !in whitelisted
    }
}
