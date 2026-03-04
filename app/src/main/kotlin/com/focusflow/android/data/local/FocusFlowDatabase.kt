package com.focusflow.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.focusflow.android.data.local.dao.AppEntryDao
import com.focusflow.android.data.local.dao.FocusModeDao
import com.focusflow.android.data.local.dao.SessionDao
import com.focusflow.android.data.local.entity.AppEntryEntity
import com.focusflow.android.data.local.entity.FocusModeEntity
import com.focusflow.android.data.local.entity.SessionEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Database(
    entities = [FocusModeEntity::class, SessionEntity::class, AppEntryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FocusFlowDatabase : RoomDatabase() {
    abstract fun focusModeDao(): FocusModeDao
    abstract fun sessionDao(): SessionDao
    abstract fun appEntryDao(): AppEntryDao

    companion object {
        const val DATABASE_NAME = "focusflow.db"
    }
}

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: String): List<String> =
        json.decodeFromString(value)

    @TypeConverter
    fun toStringList(list: List<String>): String =
        json.encodeToString(list)
}
