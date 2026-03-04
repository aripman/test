package com.focusflow.android.core.di

import android.content.Context
import androidx.room.Room
import com.focusflow.android.data.local.FocusFlowDatabase
import com.focusflow.android.data.local.dao.AppEntryDao
import com.focusflow.android.data.local.dao.FocusModeDao
import com.focusflow.android.data.local.dao.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFocusFlowDatabase(@ApplicationContext context: Context): FocusFlowDatabase =
        Room.databaseBuilder(
            context,
            FocusFlowDatabase::class.java,
            FocusFlowDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideFocusModeDao(db: FocusFlowDatabase): FocusModeDao = db.focusModeDao()

    @Provides
    fun provideSessionDao(db: FocusFlowDatabase): SessionDao = db.sessionDao()

    @Provides
    fun provideAppEntryDao(db: FocusFlowDatabase): AppEntryDao = db.appEntryDao()
}
