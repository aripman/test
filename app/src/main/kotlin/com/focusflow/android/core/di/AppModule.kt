package com.focusflow.android.core.di

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    @Singleton
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    @Provides
    @Singleton
    fun provideAppOpsManager(@ApplicationContext context: Context): AppOpsManager =
        context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
}
