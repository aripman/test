package com.focusflow.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_modes")
data class FocusModeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    @ColumnInfo(name = "icon_name") val iconName: String,
    @ColumnInfo(name = "color_seed") val colorSeed: Long,
    @ColumnInfo(name = "allowed_apps") val allowedApps: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "is_default") val isDefault: Boolean
)
