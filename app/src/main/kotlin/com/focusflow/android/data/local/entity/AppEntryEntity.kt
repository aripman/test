package com.focusflow.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "app_entries",
    primaryKeys = ["mode_id", "package_name"]
)
data class AppEntryEntity(
    @ColumnInfo(name = "mode_id") val modeId: Long,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "app_name") val appName: String,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)
