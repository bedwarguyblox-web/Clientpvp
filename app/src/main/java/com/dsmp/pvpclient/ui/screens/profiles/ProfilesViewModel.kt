package com.dsmp.pvpclient.ui.screens.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.domain.model.Profile
import com.dsmp.pvpclient.domain.model.ProfileType
import com.dsmp.pvpclient.domain.usecase.ManageProfilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfilesUiState(
    val profiles: List<Profile>         = emptyList(),
    val activeProfileId: Long?          = null,
    val showCreateDialog: Boolean       = false,
    val editingProfile: Profile?        = null,
    val newProfileName: String          = "",
    val newProfileType: ProfileType     = ProfileType.CUSTOM,
    val newProfilePerfMode: PerformanceMode = PerformanceMode.BALANCED,
    val deleteConfirmProfile: Profile?  = null
)

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val useCase: ManageProfilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfilesUiState())
    val uiState: StateFlow<ProfilesUiState> = _uiState

    init {
        viewModelScope.launch {
            useCase.seedDefaults()
            useCase.profiles.collect { profiles ->
                _uiState.value = _uiState.value.copy(
                    profiles        = profiles,
                    activeProfileId = profiles.firstOrNull { it.isActive }?.id
                )
            }
        }
    }

    // ── Dialog control ─────────────────────────────────────────────────────
    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true, newProfileName = "", newProfileType = ProfileType.CUSTOM) }
    fun dismissCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }
    fun setNewProfileName(name: String) { _uiState.value = _uiState.value.copy(newProfileName = name) }
    fun setNewProfileType(type: ProfileType) { _uiState.value = _uiState.value.copy(newProfileType = type) }
    fun setNewProfilePerfMode(mode: PerformanceMode) { _uiState.value = _uiState.value.copy(newProfilePerfMode = mode) }

    fun confirmCreate() {
        val name = _uiState.value.newProfileName.trim().ifBlank { _uiState.value.newProfileType.displayName }
        viewModelScope.launch {
            useCase.createProfile(
                Profile(
                    name            = name,
                    type            = _uiState.value.newProfileType,
                    performanceMode = _uiState.value.newProfilePerfMode,
                    iconColorHex    = _uiState.value.newProfileType.accentHex
                )
            )
            _uiState.value = _uiState.value.copy(showCreateDialog = false)
        }
    }

    // ── Actions ────────────────────────────────────────────────────────────
    fun activateProfile(profile: Profile) {
        viewModelScope.launch { useCase.activateProfile(profile.id) }
    }

    fun duplicateProfile(profile: Profile) {
        viewModelScope.launch { useCase.duplicateProfile(profile) }
    }

    fun requestDeleteProfile(profile: Profile) {
        _uiState.value = _uiState.value.copy(deleteConfirmProfile = profile)
    }

    fun cancelDeleteProfile() {
        _uiState.value = _uiState.value.copy(deleteConfirmProfile = null)
    }

    fun confirmDeleteProfile() {
        val profile = _uiState.value.deleteConfirmProfile ?: return
        viewModelScope.launch {
            useCase.deleteProfile(profile)
            _uiState.value = _uiState.value.copy(deleteConfirmProfile = null)
        }
    }
}
