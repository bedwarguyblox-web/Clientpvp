package com.dsmp.pvpclient.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dsmp.pvpclient.domain.model.PerformanceMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dsmp_settings")

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ── Keys ────────────────────────────────────────────────────────────────
    object Keys {
        val PERFORMANCE_MODE       = stringPreferencesKey("performance_mode")
        val AMOLED_MODE            = booleanPreferencesKey("amoled_mode")
        val ACCENT_COLOR           = stringPreferencesKey("accent_color")
        val ANIMATION_SPEED        = floatPreferencesKey("animation_speed")
        val SOUND_ENABLED          = booleanPreferencesKey("sound_enabled")
        val SOUND_VOLUME           = floatPreferencesKey("sound_volume")
        val OVERLAY_ENABLED        = booleanPreferencesKey("overlay_enabled")
        val FIRST_LAUNCH           = booleanPreferencesKey("first_launch")
        val MINECRAFT_PACKAGE      = stringPreferencesKey("minecraft_package")
        val UI_SCALE               = floatPreferencesKey("ui_scale")
        val NOTIFICATIONS_ENABLED  = booleanPreferencesKey("notifications_enabled")
    }

    // ── Flows ────────────────────────────────────────────────────────────────
    val performanceMode: Flow<PerformanceMode> = context.dataStore.data.map { prefs ->
        PerformanceMode.fromName(prefs[Keys.PERFORMANCE_MODE] ?: PerformanceMode.BALANCED.name)
    }

    val amoledMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AMOLED_MODE] ?: false
    }

    val accentColor: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACCENT_COLOR] ?: "#00E676"
    }

    val animationSpeed: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.ANIMATION_SPEED] ?: 1.0f
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.SOUND_ENABLED] ?: true
    }

    val soundVolume: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.SOUND_VOLUME] ?: 0.7f
    }

    val overlayEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.OVERLAY_ENABLED] ?: false
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.FIRST_LAUNCH] ?: true
    }

    val minecraftPackage: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.MINECRAFT_PACKAGE] ?: "com.mojang.minecraftpe"
    }

    val uiScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[Keys.UI_SCALE] ?: 1.0f
    }

    // ── Setters ──────────────────────────────────────────────────────────────
    suspend fun setPerformanceMode(mode: PerformanceMode) {
        context.dataStore.edit { it[Keys.PERFORMANCE_MODE] = mode.name }
    }

    suspend fun setAmoledMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AMOLED_MODE] = enabled }
    }

    suspend fun setAccentColor(hex: String) {
        context.dataStore.edit { it[Keys.ACCENT_COLOR] = hex }
    }

    suspend fun setAnimationSpeed(speed: Float) {
        context.dataStore.edit { it[Keys.ANIMATION_SPEED] = speed }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setSoundVolume(volume: Float) {
        context.dataStore.edit { it[Keys.SOUND_VOLUME] = volume }
    }

    suspend fun setOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.OVERLAY_ENABLED] = enabled }
    }

    suspend fun markFirstLaunchComplete() {
        context.dataStore.edit { it[Keys.FIRST_LAUNCH] = false }
    }

    suspend fun setMinecraftPackage(pkg: String) {
        context.dataStore.edit { it[Keys.MINECRAFT_PACKAGE] = pkg }
    }

    suspend fun setUiScale(scale: Float) {
        context.dataStore.edit { it[Keys.UI_SCALE] = scale }
    }
}
