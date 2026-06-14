package com.dsmp.pvpclient.di

import android.content.Context
import androidx.room.Room
import com.dsmp.pvpclient.data.database.AppDatabase
import com.dsmp.pvpclient.data.database.Converters
import com.dsmp.pvpclient.data.database.dao.HudLayoutDao
import com.dsmp.pvpclient.data.database.dao.ProfileDao
import com.dsmp.pvpclient.data.database.dao.ResourcePackDao
import com.dsmp.pvpclient.data.database.dao.StatisticsDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideConverters(): Converters = Converters()

    @Provides fun provideProfileDao(db: AppDatabase): ProfileDao         = db.profileDao()
    @Provides fun provideResourcePackDao(db: AppDatabase): ResourcePackDao = db.resourcePackDao()
    @Provides fun provideStatisticsDao(db: AppDatabase): StatisticsDao   = db.statisticsDao()
    @Provides fun provideHudLayoutDao(db: AppDatabase): HudLayoutDao     = db.hudLayoutDao()
}
