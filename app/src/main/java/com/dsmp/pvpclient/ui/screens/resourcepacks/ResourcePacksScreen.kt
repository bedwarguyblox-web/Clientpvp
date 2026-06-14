package com.dsmp.pvpclient.ui.screens.resourcepacks

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.PackCategory
import com.dsmp.pvpclient.domain.model.ResourcePack
import com.dsmp.pvpclient.ui.components.DSMPCard
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import com.dsmp.pvpclient.ui.theme.SurfaceDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcePacksScreen(viewModel: ResourcePacksViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    // File picker launcher
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onFilePickResult(it) } }

    // Show snackbar on import error
    LaunchedEffect(uiState.importError) {
        uiState.importError?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearImportError()
        }
    }

    // Import dialog
    if (uiState.showImportDialog) {
        ImportPackDialog(
            packName     = uiState.importDialogPackName,
            category     = uiState.importDialogCategory,
            onNameChange = viewModel::setImportPackName,
            onCategoryChange = viewModel::setImportCategory,
            onConfirm    = viewModel::confirmImport,
            onDismiss    = viewModel::dismissImportDialog
        )
    }

    Scaffold(
        containerColor = SurfaceDeep,
        topBar  = { DSMPTopBar(title = "Resource Packs") },
        snackbarHost = { SnackbarHost(snackbarHost) },
        floatingActionButton = {
            FloatingActionButton(
                onClick          = { filePicker.launch("*/*") },
                containerColor   = Emerald,
                contentColor     = SurfaceDeep
            ) {
                if (uiState.isImporting) CircularProgressIndicator(color = SurfaceDeep, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                else Icon(Icons.Rounded.Add, contentDescription = "Import Pack")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Search bar ─────────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                modifier      = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder   = { Text("Search packs…", color = OnSurfaceSecondary) },
                leadingIcon   = { Icon(Icons.Rounded.Search, null, tint = OnSurfaceSecondary) },
                singleLine    = true,
                shape         = RoundedCornerShape(16.dp)
            )

            // ── Category chips ─────────────────────────────────────────────
            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick  = { viewModel.selectCategory(null) },
                        label    = { Text("All") },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald.copy(alpha = 0.2f),
                            selectedLabelColor     = Emerald
                        )
                    )
                }
                items(PackCategory.entries) { cat ->
                    FilterChip(
                        selected = uiState.selectedCategory == cat,
                        onClick  = { viewModel.selectCategory(cat) },
                        label    = { Text("${cat.emoji} ${cat.displayName}") },
                        colors   = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald.copy(alpha = 0.2f),
                            selectedLabelColor     = Emerald
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Pack list ──────────────────────────────────────────────────
            if (uiState.packs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Layers, null, tint = OnSurfaceSecondary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No packs found", color = OnSurfaceSecondary)
                        Text("Tap + to import a pack", style = MaterialTheme.typography.bodySmall, color = OnSurfaceSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.packs, key = { it.id }) { pack ->
                        PackItem(
                            pack     = pack,
                            onToggle = { viewModel.togglePack(pack) },
                            onDelete = if (!pack.isBuiltIn) ({ viewModel.deletePack(pack) }) else null
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PackItem(
    pack: ResourcePack,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?
) {
    DSMPCard(
        glowColor = if (pack.isEnabled) Emerald.copy(alpha = 0.35f) else null
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category emoji
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(pack.category.emoji, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = pack.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "${pack.category.displayName} • ${pack.fileSizeFormatted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
                if (pack.description.isNotBlank()) {
                    Text(
                        text    = pack.description,
                        style   = MaterialTheme.typography.bodySmall,
                        color   = OnSurfaceSecondary,
                        maxLines = 1
                    )
                }
            }

            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }

            Switch(
                checked         = pack.isEnabled,
                onCheckedChange = { onToggle() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor      = Emerald,
                    checkedTrackColor      = Emerald.copy(alpha = 0.3f),
                    uncheckedThumbColor    = OnSurfaceSecondary,
                    uncheckedTrackColor    = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportPackDialog(
    packName: String,
    category: PackCategory,
    onNameChange: (String) -> Unit,
    onCategoryChange: (PackCategory) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title  = { Text("Import Resource Pack") },
        text   = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = packName,
                    onValueChange = onNameChange,
                    label         = { Text("Pack name") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded        = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value         = "${category.emoji} ${category.displayName}",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Category") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        PackCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text    = { Text("${cat.emoji} ${cat.displayName}") },
                                onClick = { onCategoryChange(cat); expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Import") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
