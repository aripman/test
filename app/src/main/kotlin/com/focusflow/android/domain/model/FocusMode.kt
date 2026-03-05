package com.focusflow.android.domain.model

data class FocusMode(
    val id: Long = 0,
    val name: String,
    val type: ModeType,
    val durationMinutes: Int,
    val iconName: String,
    val colorSeed: Long,
    val allowedApps: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isDefault: Boolean = false
)

enum class ModeType { WORK, STUDY, SLEEP, CUSTOM }
