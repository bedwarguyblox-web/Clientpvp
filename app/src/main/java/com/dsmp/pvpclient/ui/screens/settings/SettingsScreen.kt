package com.dsmp.pvpclient.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateToPerformance: () -> Unit = {},
    onNavigateToHud: () -> Unit         = {},
    onNavigateToAppearance: () -> Unit  = {},
    viewModel: SettingsViewModel        = hiltViewModel()
) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar           = { DSMPTopBar(title = "Settings") },
        containerColor   = SurfaceDeep
    ) { padding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // ── General ────────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.Settings, title = "General") }

            item {
                SettingsNavRow(
                    icon  = Icons.Rounded.Layers,
                    title = "Performance Centre",
                    sub   = state.performanceMode.displayName,
                    onClick = onNavigateToPerformance
                )
            }
            item {
                SettingsNavRow(
                    icon  = Icons.Rounded.DashboardCustomize,
                    title = "HUD Editor",
                    sub   = "Customise overlay layout",
                    onClick = onNavigateToHud
                )
            }

            // ── Appearance ─────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.Palette, title = "Appearance") }

            item {
                SettingsSwitchRow(
                    icon    = Icons.Rounded.Contrast,
                    title   = "AMOLED Mode",
                    sub     = "Pure black background",
                    checked = state.amoledMode,
                    onToggle = viewModel::setAmoledMode
                )
            }
            item {
                AccentColorRow(
                    currentHex   = state.accentColorHex,
                    onColorPicked = viewModel::setAccentColor
                )
            }
            item {
                SettingsSliderRow(
                    icon    = Icons.Rounded.Animation,
                    title   = "Animation Speed",
                    sub     = "${"%.1f".format(state.animationSpeed)}×",
                    value   = state.animationSpeed,
                    range   = 0.25f..2.0f,
                    onValueChange = viewModel::setAnimationSpeed
                )
            }
            item {
                SettingsSliderRow(
                    icon    = Icons.Rounded.AspectRatio,
                    title   = "UI Scale",
                    sub     = "${"%.0f".format(state.uiScale * 100)}%",
                    value   = state.uiScale,
                    range   = 0.75f..1.5f,
                    onValueChange = viewModel::setUiScale
                )
            }

            // ── Audio ──────────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.VolumeUp, title = "Audio") }

            item {
                SettingsSwitchRow(
                    icon    = Icons.Rounded.MusicNote,
                    title   = "UI Sounds",
                    sub     = "Menu clicks and toggles",
                    checked = state.soundEnabled,
                    onToggle = viewModel::setSoundEnabled
                )
            }
            item {
                AnimatedVisibility(state.soundEnabled) {
                    SettingsSliderRow(
                        icon    = Icons.Rounded.VolumeDown,
                        title   = "Volume",
                        sub     = "${(state.soundVolume * 100).toInt()}%",
                        value   = state.soundVolume,
                        range   = 0f..1f,
                        onValueChange = viewModel::setSoundVolume
                    )
                }
            }

            // ── Overlay ────────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.Layers, title = "Game Overlay") }

            item {
                SettingsSwitchRow(
                    icon    = Icons.Rounded.BarChart,
                    title   = "Enable Overlay",
                    sub     = "FPS / CPS counter on top of game",
                    checked = state.overlayEnabled,
                    onToggle = { enabled ->
                        if (enabled && !Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            viewModel.setOverlayEnabled(enabled)
                        }
                    }
                )
            }

            // ── PvP ────────────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.Shield, title = "PvP") }

            item {
                SettingsInfoRow(
                    icon  = Icons.Rounded.TouchApp,
                    title = "CPS Tracking",
                    sub   = "Tap the screen while playing to record CPS via overlay"
                )
            }
            item {
                SettingsInfoRow(
                    icon  = Icons.Rounded.Category,
                    title = "Pack Categories",
                    sub   = "Manage from the Resource Packs screen"
                )
            }

            // ── Advanced ──────────────────────────────────────────────────
            item { SettingsSectionHeader(icon = Icons.Rounded.Code, title = "Advanced") }

            item {
                var showPackageDialog by remember { mutableStateOf(false) }
                SettingsNavRow(
                    icon    = Icons.Rounded.Android,
                    title   = "Minecraft Package",
                    sub     = state.minecraftPackage,
                    onClick = { showPackageDialog = true }
                )
                if (showPackageDialog) {
                    PackageNameDialog(
                        currentValue = state.minecraftPackage,
                        onConfirm    = { viewModel.setMinecraftPackage(it); showPackageDialog = false },
                        onDismiss    = { showPackageDialog = false }
                    )
                }
            }
            item {
                SettingsNavRow(
                    icon    = Icons.Rounded.Info,
                    title   = "About",
                    sub     = "DSMP PvP Client v1.0.0",
                    onClick = {}
                )
            }
            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ── Reusable row composables ──────────────────────────────────────────────────

@Composable
private fun SettingsSectionHeader(icon: ImageVector, title: String) {
    Spacer(Modifier.height(12.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Icon(icon, null, tint = Emerald, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text     = title.uppercase(),
            style    = MaterialTheme.typography.labelSmall,
            color    = Emerald,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    sub: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        color  = SurfaceCard,
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = OnSurfaceSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = OnSurfacePrimary, fontWeight = FontWeight.Medium)
                Text(sub, color = OnSurfaceTertiary, style = MaterialTheme.typography.bodySmall)
            }
            Switch(
                checked        = checked,
                onCheckedChange = onToggle,
                colors         = SwitchDefaults.colors(
                    checkedThumbColor  = Emerald,
                    checkedTrackColor  = EmeraldDeep
                )
            )
        }
    }
}

@Composable
private fun SettingsSliderRow(
    icon: ImageVector,
    title: String,
    sub: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Surface(
        color  = SurfaceCard,
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = OnSurfaceSecondary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, color = OnSurfacePrimary, fontWeight = FontWeight.Medium)
                    Text(sub, color = OnSurfaceTertiary, style = MaterialTheme.typography.bodySmall)
                }
            }
            Slider(
                value         = value,
                onValueChange = onValueChange,
                valueRange    = range,
                modifier      = Modifier.padding(top = 4.dp),
                colors        = SliderDefaults.colors(thumbColor = Emerald, activeTrackColor = Emerald)
            )
        }
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    sub: String,
    onClick: () -> Unit
) {
    Surface(
        color    = SurfaceCard,
        shape    = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = OnSurfaceSecondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = OnSurfacePrimary, fontWeight = FontWeight.Medium)
                Text(sub, color = OnSurfaceTertiary, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = OnSurfaceTertiary, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, title: String, sub: String) {
    Surface(
        color  = SurfaceCard,
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = OnSurfacePrimary, fontWeight = FontWeight.Medium)
                Text(sub, color = OnSurfaceTertiary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ── Accent colour picker ──────────────────────────────────────────────────────
private val accentOptions = listOf(
    "#00E676" to "Green",
    "#448AFF" to "Blue",
    "#FF4444" to "Red",
    "#FFD600" to "Yellow",
    "#AA00FF" to "Purple",
    "#FF4081" to "Pink",
    "#00CFFF" to "Cyan",
    "#FF8800" to "Orange"
)

@Composable
private fun AccentColorRow(currentHex: String, onColorPicked: (String) -> Unit) {
    Surface(
        color  = SurfaceCard,
        shape  = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Circle, null, tint = parseHexColor(currentHex),
                    modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Text("Accent Colour", color = OnSurfacePrimary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                accentOptions.forEach { (hex, _) ->
                    val color   = parseHexColor(hex)
                    val selected = hex.equals(currentHex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { onColorPicked(hex) }
                            .then(
                                if (selected) Modifier.border(
                                    width = 2.dp, color = Color.White, shape = CircleShape
                                ) else Modifier
                            )
                    )
                }
            }
        }
    }
}

// ── Package name dialog ───────────────────────────────────────────────────────
@Composable
private fun PackageNameDialog(
    currentValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = SurfaceCard,
        title = { Text("Minecraft Package", fontWeight = FontWeight.Bold, color = OnSurfacePrimary) },
        text  = {
            OutlinedTextField(
                value         = text,
                onValueChange = { text = it },
                label         = { Text("Package name") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Emerald,
                    focusedLabelColor  = Emerald,
                    cursorColor        = Emerald
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.trim()) }, enabled = text.isNotBlank()) {
                Text("Save", color = Emerald)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = OnSurfaceSecondary) }
        }
    )
}

// ── Colour helper ─────────────────────────────────────────────────────────────
private fun parseHexColor(hex: String): Color = try {
    val clean = hex.removePrefix("#")
    Color(android.graphics.Color.parseColor("#$clean"))
} catch (_: Exception) { Color(0xFF00E676) }

// Needs @Composable context for border — helper extension
@Composable
private fun Modifier.border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.then(androidx.compose.foundation.border(width, color, shape))
