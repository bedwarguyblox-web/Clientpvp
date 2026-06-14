package com.dsmp.pvpclient.ui.screens.performance

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.ui.components.DSMPCard
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.components.UsageBar
import com.dsmp.pvpclient.ui.theme.AccentOrange
import com.dsmp.pvpclient.ui.theme.AccentRed
import com.dsmp.pvpclient.ui.theme.AccentYellow
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import com.dsmp.pvpclient.ui.theme.SurfaceDeep

@Composable
fun PerformanceScreen(
    onBack: () -> Unit,
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val info    = uiState.deviceInfo

    Scaffold(
        containerColor = SurfaceDeep,
        topBar = {
            DSMPTopBar(
                title  = "Performance Centre",
                onBack = onBack,
                actions = {
                    IconButton(onClick = viewModel::refreshDeviceInfo) {
                        Icon(Icons.Rounded.Refresh, "Refresh", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Device info ────────────────────────────────────────────────
            DSMPCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Memory, null, tint = Emerald, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("DEVICE INFO", style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                }
                Spacer(Modifier.height(12.dp))

                InfoRow("Device",   "${info.manufacturer} ${info.model}")
                InfoRow("Android",  "Android ${info.androidVersion} (API ${info.sdkVersion})")
                InfoRow("CPU ABI",  info.cpuAbi)
            }

            // ── RAM usage ──────────────────────────────────────────────────
            DSMPCard {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RAM Usage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${info.ramUsedMb}MB / ${info.totalRamMb}MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = ramColor(info.ramUsageFraction)
                    )
                }
                Spacer(Modifier.height(8.dp))
                UsageBar(
                    fraction  = info.ramUsageFraction,
                    fillColor = ramColor(info.ramUsageFraction)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${(info.ramUsageFraction * 100).toInt()}% used  •  ${info.availableRamMb}MB free",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
            }

            // ── Storage usage ──────────────────────────────────────────────
            DSMPCard {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Internal Storage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${"%.1f".format(info.storageUsedGb)}GB / ${"%.1f".format(info.totalStorageGb)}GB",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald
                    )
                }
                Spacer(Modifier.height(8.dp))
                UsageBar(fraction = info.storageUsageFraction, fillColor = AccentBlue)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${(info.storageUsageFraction * 100).toInt()}% used  •  ${"%.1f".format(info.availableStorageGb)}GB free",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
            }

            // ── Performance presets ────────────────────────────────────────
            Text(
                "Performance Presets".uppercase(),
                style        = MaterialTheme.typography.labelSmall,
                fontWeight   = FontWeight.Bold,
                color        = OnSurfaceSecondary,
                modifier     = Modifier.padding(top = 4.dp, start = 4.dp)
            )

            PerformanceMode.entries.forEach { mode ->
                PerformanceModeCard(
                    mode       = mode,
                    isSelected = uiState.selectedMode == mode,
                    onClick    = { viewModel.selectMode(mode) }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PerformanceModeCard(
    mode: PerformanceMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Emerald else com.dsmp.pvpclient.ui.theme.BorderDark

    DSMPCard(
        onClick     = onClick,
        glowColor   = if (isSelected) Emerald.copy(alpha = 0.4f) else null,
        borderColor = borderColor
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mode.emoji, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    mode.displayName,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = if (isSelected) Emerald else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    mode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Render: ${mode.renderDistance} chunks  •  Particles: ${if (mode.particlesEnabled) "On" else "Off"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceSecondary
                )
            }
            if (isSelected) {
                Icon(Icons.Rounded.Check, null, tint = Emerald, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceSecondary)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun ramColor(fraction: Float): Color = when {
    fraction > 0.85f -> AccentRed
    fraction > 0.65f -> AccentOrange
    fraction > 0.45f -> AccentYellow
    else             -> Emerald
}

private val AccentBlue = Color(0xFF448AFF)
