package com.focusflow.android.data.local.dao

import androidx.room.*
import com.focusflow.android.data.local.entity.AppEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppEntryDao {
    @Query("SELECT * FROM app_entries WHERE mode_id = :modeId")
    fun getAppsForMode(modeId: Long): Flow<List<AppEntryEntity>>

    @Query("SELECT package_name FROM app_entries WHERE mode_id = :modeId")
    suspend fun getPackageNamesForMode(modeId: Long): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntryEntity)

    @Query("DELETE FROM app_entries WHERE mode_id = :modeId AND package_name = :packageName")
    suspend fun removeApp(modeId: Long, packageName: String)

    @Query("DELETE FROM app_entries WHERE mode_id = :modeId")
    suspend fun clearAppsForMode(modeId: Long)

    @Transaction
    suspend fun replaceAppsForMode(modeId: Long, apps: List<AppEntryEntity>) {
        clearAppsForMode(modeId)
        apps.forEach { insertApp(it) }
    }
}
