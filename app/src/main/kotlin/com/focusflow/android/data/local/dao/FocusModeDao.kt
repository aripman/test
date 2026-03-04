package com.focusflow.android.data.local.dao

import androidx.room.*
import com.focusflow.android.data.local.entity.FocusModeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusModeDao {
    @Query("SELECT * FROM focus_modes ORDER BY created_at DESC")
    fun getAllModes(): Flow<List<FocusModeEntity>>

    @Query("SELECT * FROM focus_modes WHERE id = :id")
    suspend fun getModeById(id: Long): FocusModeEntity?

    @Query("SELECT * FROM focus_modes WHERE is_default = 1 LIMIT 1")
    suspend fun getDefaultMode(): FocusModeEntity?

    @Upsert
    suspend fun upsertMode(mode: FocusModeEntity): Long

    @Delete
    suspend fun deleteMode(mode: FocusModeEntity)
}
