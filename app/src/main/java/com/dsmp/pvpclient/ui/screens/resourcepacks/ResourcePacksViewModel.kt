package com.dsmp.pvpclient.ui.screens.resourcepacks

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsmp.pvpclient.domain.model.PackCategory
import com.dsmp.pvpclient.domain.model.ResourcePack
import com.dsmp.pvpclient.domain.usecase.ManageResourcePacksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResourcePacksUiState(
    val packs: List<ResourcePack>     = emptyList(),
    val selectedCategory: PackCategory? = null,
    val searchQuery: String           = "",
    val isImporting: Boolean          = false,
    val importError: String?          = null,
    val showImportDialog: Boolean     = false,
    val importDialogPackName: String  = "",
    val importDialogCategory: PackCategory = PackCategory.CUSTOM,
    val pendingImportUri: Uri?        = null
)

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ResourcePacksViewModel @Inject constructor(
    private val useCase: ManageResourcePacksUseCase
) : ViewModel() {

    private val _searchQuery       = MutableStateFlow("")
    private val _selectedCategory  = MutableStateFlow<PackCategory?>(null)
    private val _uiState           = MutableStateFlow(ResourcePacksUiState())
    val uiState: StateFlow<ResourcePacksUiState> = _uiState

    // Reactive pack list that responds to search + category filter
    private val filteredPacks = combine(
        _searchQuery.debounce(200),
        _selectedCategory
    ) { query, category -> Pair(query, category) }
        .flatMapLatest { (query, category) ->
            when {
                query.isNotBlank() -> useCase.search(query)
                category != null   -> useCase.packsByCategory(category)
                else               -> useCase.allPacks
            }
        }

    init {
        viewModelScope.launch {
            useCase.seedBuiltIns()
        }
        viewModelScope.launch {
            filteredPacks.collect { packs ->
                _uiState.value = _uiState.value.copy(packs = packs)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: PackCategory?) {
        _selectedCategory.value = category
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun togglePack(pack: ResourcePack) {
        viewModelScope.launch { useCase.togglePack(pack) }
    }

    fun deletePack(pack: ResourcePack) {
        viewModelScope.launch { useCase.deletePack(pack) }
    }

    /** Step 1 — user picked a file URI; show the import dialog to name it. */
    fun onFilePickResult(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            pendingImportUri  = uri,
            showImportDialog  = true,
            importDialogPackName = "",
            importDialogCategory = PackCategory.CUSTOM
        )
    }

    fun setImportPackName(name: String) {
        _uiState.value = _uiState.value.copy(importDialogPackName = name)
    }

    fun setImportCategory(category: PackCategory) {
        _uiState.value = _uiState.value.copy(importDialogCategory = category)
    }

    fun dismissImportDialog() {
        _uiState.value = _uiState.value.copy(showImportDialog = false, pendingImportUri = null)
    }

    /** Step 2 — confirm import with the user's chosen name and category. */
    fun confirmImport() {
        val uri      = _uiState.value.pendingImportUri ?: return
        val name     = _uiState.value.importDialogPackName.trim().ifBlank { "Imported Pack" }
        val category = _uiState.value.importDialogCategory

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, showImportDialog = false)
            val result = useCase.importPack(uri, name, category)
            _uiState.value = _uiState.value.copy(
                isImporting      = false,
                pendingImportUri = null,
                importError      = if (result == null) "Import failed. Please try again." else null
            )
        }
    }

    fun clearImportError() {
        _uiState.value = _uiState.value.copy(importError = null)
    }
}
