package com.focusflow.android.data.repository

import com.focusflow.android.data.local.dao.FocusModeDao
import com.focusflow.android.data.local.entity.FocusModeEntity
import com.focusflow.android.domain.model.FocusMode
import com.focusflow.android.domain.model.ModeType
import com.focusflow.android.domain.repository.FocusModeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FocusModeRepositoryImpl @Inject constructor(
    private val dao: FocusModeDao,
    private val json: Json
) : FocusModeRepository {

    override fun getAllModes(): Flow<List<FocusMode>> =
        dao.getAllModes().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getModeById(id: Long): FocusMode? =
        dao.getModeById(id)?.toDomain()

    override suspend fun getDefaultMode(): FocusMode? =
        dao.getDefaultMode()?.toDomain()

    override suspend fun saveMode(mode: FocusMode): Long =
        dao.upsertMode(mode.toEntity())

    override suspend fun deleteMode(mode: FocusMode) =
        dao.deleteMode(mode.toEntity())

    private fun FocusModeEntity.toDomain() = FocusMode(
        id = id,
        name = name,
        type = ModeType.valueOf(type),
        durationMinutes = durationMinutes,
        iconName = iconName,
        colorSeed = colorSeed,
        allowedApps = try { json.decodeFromString(allowedApps) } catch (e: Exception) { emptyList() },
        createdAt = createdAt,
        isDefault = isDefault
    )

    private fun FocusMode.toEntity() = FocusModeEntity(
        id = id,
        name = name,
        type = type.name,
        durationMinutes = durationMinutes,
        iconName = iconName,
        colorSeed = colorSeed,
        allowedApps = json.encodeToString(allowedApps),
        createdAt = createdAt,
        isDefault = isDefault
    )
}
