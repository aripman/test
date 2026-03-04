package com.focusflow.android.domain.repository

import com.focusflow.android.domain.model.FocusMode
import kotlinx.coroutines.flow.Flow

interface FocusModeRepository {
    fun getAllModes(): Flow<List<FocusMode>>
    suspend fun getModeById(id: Long): FocusMode?
    suspend fun getDefaultMode(): FocusMode?
    suspend fun saveMode(mode: FocusMode): Long
    suspend fun deleteMode(mode: FocusMode)
}
