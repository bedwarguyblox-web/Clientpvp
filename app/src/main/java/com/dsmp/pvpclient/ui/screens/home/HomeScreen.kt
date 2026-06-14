package com.dsmp.pvpclient.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.domain.model.PerformanceMode
import com.dsmp.pvpclient.ui.components.DSMPCard
import com.dsmp.pvpclient.ui.components.PulseDot
import com.dsmp.pvpclient.ui.components.StatChip
import com.dsmp.pvpclient.ui.theme.CosmicBlack
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.EmeraldDark
import com.dsmp.pvpclient.ui.theme.GlowGreen
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import com.dsmp.pvpclient.ui.theme.SurfaceDeep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToPerformance: () -> Unit,
    onNavigateToHud: () -> Unit,
    onNavigateToPacks: () -> Unit,
    onNavigateToProfiles: () -> Unit
) {
    val uiState     by viewModel.uiState.collectAsStateWithLifecycle()
    val perfMode    by viewModel.performanceMode.collectAsStateWithLifecycle()

    // Show "Minecraft not installed" dialog
    if (uiState.isMinecraftInstalled == false) {
        AlertDialog(
            onDismissRequest = { viewModel.clearLaunchError() },
            title  = { Text("Minecraft Not Found") },
            text   = { Text("Minecraft Bedrock Edition is not installed. Install it from the Play Store to continue.") },
            confirmButton = {
                Button(onClick = { viewModel.openPlayStore(); viewModel.clearLaunchError() }) {
                    Text("Open Play Store")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearLaunchError() }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        containerColor = SurfaceDeep,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Gamepad, null, tint = Emerald, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("DSMP PvP Client", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Emerald)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.Notifications, null, tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CosmicBlack)
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
            Spacer(Modifier.height(8.dp))

            // ── LAUNCH BUTTON ──────────────────────────────────────────────
            LaunchButton(
                isLaunching = uiState.isLaunching,
                onClick     = viewModel::launchMinecraft
            )

            Spacer(Modifier.height(4.dp))

            // ── ACTIVE PROFILE + PACK ROW ──────────────────────────────────
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DSMPCard(
                    modifier  = Modifier.weight(1f),
                    onClick   = onNavigateToProfiles,
                    glowColor = uiState.activeProfile?.let {
                        try { Color(android.graphics.Color.parseColor(it.iconColorHex)) }
                        catch (_: Exception) { Emerald }
                    }?.copy(alpha = 0.4f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Person, null, tint = Emerald, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PROFILE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = uiState.activeProfile?.name ?: "No Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = uiState.activeProfile?.type?.displayName ?: "—",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceSecondary
                    )
                }

                DSMPCard(
                    modifier  = Modifier.weight(1f),
                    onClick   = onNavigateToPacks,
                    glowColor = if (uiState.activeResourcePack != null) Emerald.copy(alpha = 0.3f) else null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Layers, null, tint = Emerald, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PACK", style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text  = uiState.activeResourcePack?.name ?: "None",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text  = uiState.activeResourcePack?.category?.displayName ?: "—",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceSecondary
                    )
                }
            }

            // ── PERFORMANCE MODE ───────────────────────────────────────────
            DSMPCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Speed, null, tint = Emerald, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("PERFORMANCE", style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PerformanceMode.entries.forEach { mode ->
                        val selected = perfMode == mode
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = selected,
                            onClick  = { viewModel.setPerformanceMode(mode) },
                            label    = {
                                Text(
                                    text  = mode.emoji,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald.copy(alpha = 0.2f),
                                selectedLabelColor     = Emerald
                            )
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "${perfMode.emoji} ${perfMode.displayName} — ${perfMode.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
            }

            // ── QUICK STATS ────────────────────────────────────────────────
            val stats = uiState.statistics
            if (stats != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatChip(
                        label    = "Sessions",
                        value    = stats.sessionsPlayed.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label    = "Total Time",
                        value    = stats.totalUsageFormatted,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label    = "Last Game",
                        value    = stats.lastLaunchFormatted,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── QUICK ACTIONS ──────────────────────────────────────────────
            DSMPCard {
                Text("Quick Actions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton("HUD Editor", Icons.Rounded.Settings, Modifier.weight(1f), onNavigateToHud)
                    QuickActionButton("Performance", Icons.Rounded.Speed, Modifier.weight(1f), onNavigateToPerformance)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LaunchButton(isLaunching: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "launchScale"
    )

    Box(
        modifier           = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(EmeraldDark.copy(alpha = 0.9f), Color(0xFF003B1E))
                )
            )
            .border(1.dp, Emerald.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        contentAlignment   = Alignment.Center
    ) {
        Button(
            onClick           = onClick,
            modifier          = Modifier.fillMaxSize(),
            interactionSource = interactionSource,
            shape             = RoundedCornerShape(20.dp),
            enabled           = !isLaunching,
            colors            = ButtonDefaults.buttonColors(
                containerColor         = Color.Transparent,
                disabledContainerColor = Color.Transparent
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isLaunching) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color    = Emerald,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier           = Modifier.size(48.dp),
                        tint               = Color.White
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text       = if (isLaunching) "LAUNCHING…" else "LAUNCH MINECRAFT",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick  = onClick,
        modifier = modifier.height(44.dp),
        shape    = RoundedCornerShape(12.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor   = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(icon, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
