package com.dsmp.pvpclient.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.domain.model.Profile
import com.dsmp.pvpclient.domain.model.ProfileType
import com.dsmp.pvpclient.ui.components.DSMPCard
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.components.PulseDot
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import com.dsmp.pvpclient.ui.theme.SurfaceDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(viewModel: ProfilesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Create dialog
    if (uiState.showCreateDialog) {
        CreateProfileDialog(
            name          = uiState.newProfileName,
            type          = uiState.newProfileType,
            perfMode      = uiState.newProfilePerfMode,
            onNameChange  = viewModel::setNewProfileName,
            onTypeChange  = viewModel::setNewProfileType,
            onPerfChange  = viewModel::setNewProfilePerfMode,
            onConfirm     = viewModel::confirmCreate,
            onDismiss     = viewModel::dismissCreateDialog
        )
    }

    // Delete confirm dialog
    uiState.deleteConfirmProfile?.let { profile ->
        AlertDialog(
            onDismissRequest = viewModel::cancelDeleteProfile,
            title  = { Text("Delete Profile") },
            text   = { Text("Delete \"${profile.name}\"? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmDeleteProfile,
                    colors  = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = viewModel::cancelDeleteProfile) { Text("Cancel") } }
        )
    }

    Scaffold(
        containerColor = SurfaceDeep,
        topBar = { DSMPTopBar("Profiles") },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = viewModel::showCreateDialog,
                containerColor = Emerald,
                contentColor   = SurfaceDeep
            ) { Icon(Icons.Rounded.Add, "New Profile") }
        }
    ) { padding ->
        if (uiState.profiles.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Person, null, tint = OnSurfaceSecondary, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No profiles yet", color = OnSurfaceSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier              = Modifier.fillMaxSize().padding(padding),
                contentPadding        = PaddingValues(16.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp)
            ) {
                items(uiState.profiles, key = { it.id }) { profile ->
                    ProfileCard(
                        profile       = profile,
                        isActive      = profile.id == uiState.activeProfileId,
                        onActivate    = { viewModel.activateProfile(profile) },
                        onDuplicate   = { viewModel.duplicateProfile(profile) },
                        onDelete      = { viewModel.requestDeleteProfile(profile) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: Profile,
    isActive: Boolean,
    onActivate: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    val accentColor = remember(profile.iconColorHex) {
        try { Color(android.graphics.Color.parseColor(profile.iconColorHex)) }
        catch (_: Exception) { Emerald }
    }

    DSMPCard(
        onClick   = onActivate,
        glowColor = if (isActive) accentColor.copy(alpha = 0.5f) else null
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(if (isActive) 2.dp else 0.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(profile.emoji, style = MaterialTheme.typography.headlineSmall)
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text       = profile.name,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    if (isActive) {
                        Spacer(Modifier.width(8.dp))
                        PulseDot(color = accentColor)
                    }
                }
                Text(
                    text  = "${profile.type.displayName} • ${profile.performanceMode.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
            }

            // Actions
            Row {
                IconButton(onClick = onDuplicate, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Rounded.ContentCopy, null, tint = OnSurfaceSecondary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
                if (isActive) {
                    Icon(Icons.Rounded.Check, null, tint = accentColor, modifier = Modifier.size(20.dp).align(Alignment.CenterVertically))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateProfileDialog(
    name: String,
    type: ProfileType,
    perfMode: PerformanceMode,
    onNameChange: (String) -> Unit,
    onTypeChange: (ProfileType) -> Unit,
    onPerfChange: (PerformanceMode) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var perfExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Profile") },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = name,
                    onValueChange = onNameChange,
                    label         = { Text("Profile name") },
                    placeholder   = { Text(type.displayName) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )

                // Profile type dropdown
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value         = "${type.emoji} ${type.displayName}",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Mode") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        ProfileType.entries.forEach { t ->
                            DropdownMenuItem(
                                text    = { Text("${t.emoji} ${t.displayName}") },
                                onClick = { onTypeChange(t); typeExpanded = false }
                            )
                        }
                    }
                }

                // Performance dropdown
                ExposedDropdownMenuBox(expanded = perfExpanded, onExpandedChange = { perfExpanded = it }) {
                    OutlinedTextField(
                        value         = "${perfMode.emoji} ${perfMode.displayName}",
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Performance") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(perfExpanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = perfExpanded, onDismissRequest = { perfExpanded = false }) {
                        PerformanceMode.entries.forEach { m ->
                            DropdownMenuItem(
                                text    = { Text("${m.emoji} ${m.displayName}") },
                                onClick = { onPerfChange(m); perfExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton  = { Button(onClick = onConfirm) { Text("Create") } },
        dismissButton  = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
