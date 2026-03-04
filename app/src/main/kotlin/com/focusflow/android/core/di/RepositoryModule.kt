package com.focusflow.android.core.di

import com.focusflow.android.data.repository.AppRepositoryImpl
import com.focusflow.android.data.repository.CalendarRepositoryImpl
import com.focusflow.android.data.repository.FocusModeRepositoryImpl
import com.focusflow.android.data.repository.SessionRepositoryImpl
import com.focusflow.android.domain.repository.AppRepository
import com.focusflow.android.domain.repository.CalendarRepository
import com.focusflow.android.domain.repository.FocusModeRepository
import com.focusflow.android.domain.repository.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindFocusModeRepository(impl: FocusModeRepositoryImpl): FocusModeRepository

    @Binds @Singleton
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

    @Binds @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds @Singleton
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}
