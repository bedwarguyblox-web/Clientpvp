package com.dsmp.pvpclient.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.data.preferences.AppPreferences
import com.dsmp.pvpclient.domain.model.PerformanceMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val performanceMode:  PerformanceMode = PerformanceMode.BALANCED,
    val amoledMode:       Boolean          = false,
    val accentColorHex:   String           = "#00E676",
    val animationSpeed:   Float            = 1.0f,
    val soundEnabled:     Boolean          = true,
    val soundVolume:      Float            = 0.7f,
    val overlayEnabled:   Boolean          = false,
    val uiScale:          Float            = 1.0f,
    val minecraftPackage: String           = "com.mojang.minecraftpe",
    val isLoading:        Boolean          = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { observePrefs() }

    /**
     * Chains two [combine] calls to stay within the 5-flow typed overload limit.
     * First combine handles appearance/audio (5 prefs); second merges with
     * the rest via flatMapLatest approach below.
     */
    private fun observePrefs() {
        // Batch 1: performanceMode, amoledMode, accentColor, animationSpeed, soundEnabled
        val batch1 = combine(
            prefs.performanceMode,
            prefs.amoledMode,
            prefs.accentColor,
            prefs.animationSpeed,
            prefs.soundEnabled
        ) { mode, amoled, accent, anim, sound ->
            Quintuple(mode, amoled, accent, anim, sound)
        }

        // Batch 2: soundVolume, overlayEnabled, uiScale, minecraftPackage
        val batch2 = combine(
            prefs.soundVolume,
            prefs.overlayEnabled,
            prefs.uiScale,
            prefs.minecraftPackage
        ) { vol, overlay, scale, pkg ->
            Quadruple(vol, overlay, scale, pkg)
        }

        viewModelScope.launch {
            combine(batch1, batch2) { b1, b2 ->
                SettingsUiState(
                    performanceMode  = b1.first,
                    amoledMode       = b1.second,
                    accentColorHex   = b1.third,
                    animationSpeed   = b1.fourth,
                    soundEnabled     = b1.fifth,
                    soundVolume      = b2.first,
                    overlayEnabled   = b2.second,
                    uiScale          = b2.third,
                    minecraftPackage = b2.fourth,
                    isLoading        = false
                )
            }.collect { state -> _uiState.update { state } }
        }
    }

    // ── Setters ──────────────────────────────────────────────────────────────
    fun setPerformanceMode(mode: PerformanceMode) = viewModelScope.launch { prefs.setPerformanceMode(mode) }
    fun setAmoledMode(enabled: Boolean)           = viewModelScope.launch { prefs.setAmoledMode(enabled) }
    fun setAccentColor(hex: String)               = viewModelScope.launch { prefs.setAccentColor(hex) }
    fun setAnimationSpeed(speed: Float)           = viewModelScope.launch { prefs.setAnimationSpeed(speed) }
    fun setSoundEnabled(enabled: Boolean)         = viewModelScope.launch { prefs.setSoundEnabled(enabled) }
    fun setSoundVolume(volume: Float)             = viewModelScope.launch { prefs.setSoundVolume(volume) }
    fun setOverlayEnabled(enabled: Boolean)       = viewModelScope.launch { prefs.setOverlayEnabled(enabled) }
    fun setUiScale(scale: Float)                  = viewModelScope.launch { prefs.setUiScale(scale) }
    fun setMinecraftPackage(pkg: String)          = viewModelScope.launch { prefs.setMinecraftPackage(pkg) }
}

// Lightweight tuple helpers — avoids a data class proliferation
private data class Quintuple<A, B, C, D, E>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E
)
private data class Quadruple<A, B, C, D>(
    val first: A, val second: B, val third: C, val fourth: D
)
