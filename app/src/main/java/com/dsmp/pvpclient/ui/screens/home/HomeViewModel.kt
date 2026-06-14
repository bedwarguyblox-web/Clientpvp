package com.dsmp.pvpclient.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.data.preferences.AppPreferences
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.domain.model.Profile
import com.dsmp.pvpclient.domain.model.ResourcePack
import com.dsmp.pvpclient.domain.model.Statistics
import com.dsmp.pvpclient.domain.usecase.LaunchMinecraftUseCase
import com.dsmp.pvpclient.domain.usecase.ManageProfilesUseCase
import com.dsmp.pvpclient.domain.usecase.ManageResourcePacksUseCase
import com.dsmp.pvpclient.data.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val activeProfile: Profile?         = null,
    val activeResourcePack: ResourcePack? = null,
    val performanceMode: PerformanceMode  = PerformanceMode.BALANCED,
    val statistics: Statistics?           = null,
    val isMinecraftInstalled: Boolean     = true,
    val isLaunching: Boolean              = false,
    val launchError: String?              = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val launchUseCase: LaunchMinecraftUseCase,
    private val profilesUseCase: ManageProfilesUseCase,
    private val packsUseCase: ManageResourcePacksUseCase,
    private val statisticsRepository: StatisticsRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    val performanceMode: StateFlow<PerformanceMode> = preferences.performanceMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PerformanceMode.BALANCED)

    init {
        seedData()
        observeActiveProfile()
        observeEnabledPacks()
        observeStatistics()
    }

    private fun seedData() {
        viewModelScope.launch {
            profilesUseCase.seedDefaults()
            packsUseCase.seedBuiltIns()
            statisticsRepository.ensureExists()
        }
    }

    private fun observeActiveProfile() {
        viewModelScope.launch {
            profilesUseCase.activeProfile.collect { profile ->
                _uiState.value = _uiState.value.copy(activeProfile = profile)
            }
        }
    }

    private fun observeEnabledPacks() {
        viewModelScope.launch {
            packsUseCase.enabledPacks.collect { packs ->
                _uiState.value = _uiState.value.copy(
                    activeResourcePack = packs.firstOrNull()
                )
            }
        }
    }

    private fun observeStatistics() {
        viewModelScope.launch {
            statisticsRepository.statistics.collect { stats ->
                _uiState.value = _uiState.value.copy(statistics = stats)
            }
        }
    }

    fun launchMinecraft() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLaunching = true, launchError = null)
            val result = launchUseCase()
            _uiState.value = _uiState.value.copy(
                isLaunching        = false,
                isMinecraftInstalled = result !is LaunchMinecraftUseCase.Result.NotInstalled,
                launchError = when (result) {
                    is LaunchMinecraftUseCase.Result.Error -> result.message
                    else -> null
                }
            )
        }
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        viewModelScope.launch { preferences.setPerformanceMode(mode) }
    }

    fun clearLaunchError() {
        _uiState.value = _uiState.value.copy(launchError = null)
    }

    fun openPlayStore() = launchUseCase.openPlayStore()
}
