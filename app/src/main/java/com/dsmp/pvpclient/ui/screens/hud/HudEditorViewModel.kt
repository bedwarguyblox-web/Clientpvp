package com.dsmp.pvpclient.ui.screens.hud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.data.repository.HudRepository
import com.dsmp.pvpclient.domain.model.HudElement
import com.dsmp.pvpclient.domain.model.HudElementType
import com.dsmp.pvpclient.domain.model.HudLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HudEditorUiState(
    val layouts: List<HudLayout>     = emptyList(),
    val activeLayout: HudLayout?     = null,
    val elements: List<HudElement>   = emptyList(),
    val selectedElementId: String?   = null,
    val isSaving: Boolean            = false,
    val saveSuccess: Boolean         = false,
    val isLoading: Boolean           = true
)

@HiltViewModel
class HudEditorViewModel @Inject constructor(
    private val hudRepository: HudRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HudEditorUiState())
    val uiState: StateFlow<HudEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            hudRepository.seedDefaultIfEmpty()
        }
        observeLayouts()
        observeActiveLayout()
    }

    private fun observeLayouts() = viewModelScope.launch {
        hudRepository.layouts.collect { layouts ->
            _uiState.update { it.copy(layouts = layouts, isLoading = false) }
        }
    }

    private fun observeActiveLayout() = viewModelScope.launch {
        hudRepository.activeLayout.collect { layout ->
            _uiState.update { state ->
                state.copy(
                    activeLayout = layout,
                    elements     = layout?.elements ?: HudLayout.default().elements
                )
            }
        }
    }

    /** Called from the drag gesture in the editor canvas. */
    fun onElementMoved(elementId: String, newX: Float, newY: Float) {
        _uiState.update { state ->
            val updated = state.elements.map { el ->
                if (el.id == elementId) el.copy(x = newX.coerceAtLeast(0f), y = newY.coerceAtLeast(0f))
                else el
            }
            state.copy(elements = updated)
        }
    }

    fun onElementSelected(elementId: String?) {
        _uiState.update { it.copy(selectedElementId = elementId) }
    }

    fun onElementScaleChanged(elementId: String, scale: Float) {
        _uiState.update { state ->
            val updated = state.elements.map { el ->
                if (el.id == elementId) el.copy(scale = scale.coerceIn(0.5f, 3.0f)) else el
            }
            state.copy(elements = updated)
        }
    }

    fun onElementOpacityChanged(elementId: String, opacity: Float) {
        _uiState.update { state ->
            val updated = state.elements.map { el ->
                if (el.id == elementId) el.copy(opacity = opacity.coerceIn(0.1f, 1.0f)) else el
            }
            state.copy(elements = updated)
        }
    }

    fun onElementVisibilityToggled(elementId: String) {
        _uiState.update { state ->
            val updated = state.elements.map { el ->
                if (el.id == elementId) el.copy(isVisible = !el.isVisible) else el
            }
            state.copy(elements = updated)
        }
    }

    fun saveLayout() = viewModelScope.launch {
        val state = _uiState.value
        _uiState.update { it.copy(isSaving = true) }

        val layoutToSave = state.activeLayout?.copy(elements = state.elements)
            ?: HudLayout(name = "My Layout", elements = state.elements, isActive = true)

        if (layoutToSave.id == 0L) {
            val id = hudRepository.save(layoutToSave)
            hudRepository.setActive(id)
        } else {
            hudRepository.update(layoutToSave)
        }
        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
    }

    fun saveAsNew(name: String) = viewModelScope.launch {
        _uiState.update { it.copy(isSaving = true) }
        val newLayout = HudLayout(
            name     = name,
            elements = _uiState.value.elements,
            isActive = true
        )
        val id = hudRepository.save(newLayout)
        hudRepository.setActive(id)
        _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
    }

    fun loadLayout(layout: HudLayout) = viewModelScope.launch {
        hudRepository.setActive(layout.id)
        _uiState.update { it.copy(elements = layout.elements, saveSuccess = false) }
    }

    fun resetToDefault() {
        _uiState.update { it.copy(elements = HudLayout.default().elements) }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    val selectedElement: HudElement?
        get() {
            val id = _uiState.value.selectedElementId ?: return null
            return _uiState.value.elements.firstOrNull { it.id == id }
        }
}
