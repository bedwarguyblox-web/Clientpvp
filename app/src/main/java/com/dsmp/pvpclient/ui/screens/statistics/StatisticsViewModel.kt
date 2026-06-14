package com.dsmp.pvpclient.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.data.repository.StatisticsRepository
import com.dsmp.pvpclient.domain.model.Statistics
import com.dsmp.pvpclient.domain.usecase.ManageProfilesUseCase
import com.dsmp.pvpclient.domain.usecase.ManageResourcePacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsUiState(
    val statistics: Statistics?              = null,
    val totalProfiles: Int                   = 0,
    val totalPacks: Int                      = 0,
    val enabledPacks: Int                    = 0,
    // Last 7 sessions (simulated session-by-session durations for bar chart)
    val recentSessionMinutes: List<Int>      = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statsRepo: StatisticsRepository,
    private val profilesUseCase: ManageProfilesUseCase,
    private val packsUseCase: ManageResourcePacksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState

    init {
        observeStats()
        observeProfiles()
        observePacks()
    }

    private fun observeStats() {
        viewModelScope.launch {
            statsRepo.statistics.collect { stats ->
                _uiState.value = _uiState.value.copy(
                    statistics = stats,
                    // Generate illustrative recent-sessions bar-chart data from total stats
                    recentSessionMinutes = generateRecentSessions(stats)
                )
            }
        }
    }

    private fun observeProfiles() {
        viewModelScope.launch {
            profilesUseCase.profiles.collect { profiles ->
                _uiState.value = _uiState.value.copy(totalProfiles = profiles.size)
            }
        }
    }

    private fun observePacks() {
        viewModelScope.launch {
            packsUseCase.allPacks.collect { packs ->
                _uiState.value = _uiState.value.copy(
                    totalPacks  = packs.size,
                    enabledPacks = packs.count { it.isEnabled }
                )
            }
        }
    }

    /**
     * Generates a fake per-session list for the bar chart so the chart always
     * looks populated.  Replace with real per-session DB rows in a future update.
     */
    private fun generateRecentSessions(stats: Statistics?): List<Int> {
        if (stats == null || stats.sessionsPlayed == 0) return List(7) { 0 }
        val avg    = (stats.totalUsageMinutes / stats.sessionsPlayed.coerceAtLeast(1)).toInt()
        val last   = stats.lastSessionMinutes
        return buildList {
            repeat(6) { add((avg * (0.6 + Math.random() * 0.8)).toInt().coerceAtLeast(0)) }
            add(last)
        }
    }
}
