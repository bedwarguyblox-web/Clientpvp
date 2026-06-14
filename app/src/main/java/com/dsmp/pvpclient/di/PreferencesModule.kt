package com.dsmp.pvpclient.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * [com.dsmp.pvpclient.data.preferences.AppPreferences] uses constructor injection
 * (@Singleton + @Inject), so no explicit @Provides binding is required.
 *
 * This module is kept as a placeholder for future preference-related bindings
 * (e.g. encrypted DataStore, multi-user support).
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule
