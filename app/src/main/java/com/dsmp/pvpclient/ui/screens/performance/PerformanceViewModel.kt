package com.dsmp.pvpclient.ui.screens.performance

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.StatFs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.data.preferences.AppPreferences
import com.dsmp.pvpclient.domain.model.PerformanceMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceInfo(
    val model: String           = Build.MODEL,
    val manufacturer: String    = Build.MANUFACTURER,
    val androidVersion: String  = Build.VERSION.RELEASE,
    val sdkVersion: Int         = Build.VERSION.SDK_INT,
    val cpuAbi: String          = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown",
    val totalRamMb: Long        = 0L,
    val availableRamMb: Long    = 0L,
    val totalStorageGb: Float   = 0f,
    val availableStorageGb: Float = 0f
) {
    val ramUsageFraction: Float
        get() = if (totalRamMb == 0L) 0f
                else 1f - availableRamMb.toFloat() / totalRamMb.toFloat()

    val storageUsageFraction: Float
        get() = if (totalStorageGb == 0f) 0f
                else 1f - availableStorageGb / totalStorageGb

    val ramUsedMb: Long  get() = totalRamMb - availableRamMb
    val storageUsedGb: Float get() = totalStorageGb - availableStorageGb
}

data class PerformanceUiState(
    val deviceInfo: DeviceInfo        = DeviceInfo(),
    val selectedMode: PerformanceMode = PerformanceMode.BALANCED
)

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerformanceUiState())
    val uiState: StateFlow<PerformanceUiState> = _uiState

    val savedMode: StateFlow<PerformanceMode> = preferences.performanceMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerformanceMode.BALANCED)

    init {
        loadDeviceInfo()
        viewModelScope.launch {
            preferences.performanceMode.collect { mode ->
                _uiState.value = _uiState.value.copy(selectedMode = mode)
            }
        }
    }

    private fun loadDeviceInfo() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo         = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val totalRamMb    = memInfo.totalMem / (1024 * 1024)
        val availableRamMb = memInfo.availMem / (1024 * 1024)

        val stat              = StatFs(context.filesDir.absolutePath)
        val totalStorageGb    = stat.totalBytes.toFloat() / (1024 * 1024 * 1024)
        val availableStorageGb = stat.availableBytes.toFloat() / (1024 * 1024 * 1024)

        _uiState.value = _uiState.value.copy(
            deviceInfo = DeviceInfo(
                totalRamMb         = totalRamMb,
                availableRamMb     = availableRamMb,
                totalStorageGb     = totalStorageGb,
                availableStorageGb = availableStorageGb
            )
        )
    }

    fun selectMode(mode: PerformanceMode) {
        viewModelScope.launch { preferences.setPerformanceMode(mode) }
    }

    fun refreshDeviceInfo() = loadDeviceInfo()
}
