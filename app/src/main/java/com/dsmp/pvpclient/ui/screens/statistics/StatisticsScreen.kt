package com.dsmp.pvpclient.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dsmp.pvpclient.ui.components.DSMPCard
import com.dsmp.pvpclient.ui.components.DSMPTopBar
import com.dsmp.pvpclient.ui.components.StatChip
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.EmeraldDark
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import com.dsmp.pvpclient.ui.theme.SurfaceDeep
import com.dsmp.pvpclient.ui.theme.SurfaceElevated

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stats   = uiState.statistics

    Scaffold(
        containerColor = SurfaceDeep,
        topBar         = { DSMPTopBar("Statistics") }
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

            // ── KPI row ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    label    = "Sessions",
                    value    = (stats?.sessionsPlayed ?: 0).toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label    = "Total Time",
                    value    = stats?.totalUsageFormatted ?: "0m",
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label    = "Last Played",
                    value    = stats?.lastLaunchFormatted ?: "Never",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    label    = "Profiles",
                    value    = uiState.totalProfiles.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label    = "Packs",
                    value    = uiState.totalPacks.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label    = "Active Packs",
                    value    = uiState.enabledPacks.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Session bar chart ──────────────────────────────────────────
            DSMPCard(contentPadding = 16.dp) {
                Text(
                    "Session History",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Last 7 sessions (minutes)",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceSecondary
                )
                Spacer(Modifier.height(16.dp))

                val data   = uiState.recentSessionMinutes
                val maxVal = data.maxOrNull()?.toFloat()?.coerceAtLeast(1f) ?: 1f

                SessionBarChart(
                    data     = data,
                    maxValue = maxVal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("7 sessions ago", style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                    Text("Latest",         style = MaterialTheme.typography.labelSmall, color = OnSurfaceSecondary)
                }
            }

            // ── Last session detail ────────────────────────────────────────
            DSMPCard(contentPadding = 16.dp) {
                Text(
                    "Last Session",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))

                val lastMin  = stats?.lastSessionMinutes ?: 0
                val hours    = lastMin / 60
                val mins     = lastMin % 60
                val duration = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Duration", style = MaterialTheme.typography.bodySmall, color = OnSurfaceSecondary)
                        Text(duration,   style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Emerald)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Launched",    style = MaterialTheme.typography.bodySmall, color = OnSurfaceSecondary)
                        Text(stats?.lastLaunchFormatted ?: "Never", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SessionBarChart(
    data: List<Int>,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier) {
        val barCount  = data.size
        val spacing   = size.width / barCount
        val barWidth  = spacing * 0.55f
        val chartH    = size.height

        data.forEachIndexed { index, value ->
            val fraction  = value.toFloat() / maxValue
            val barHeight = fraction * chartH
            val left      = index * spacing + (spacing - barWidth) / 2f
            val top       = chartH - barHeight

            // Bar
            drawRoundRect(
                brush       = Brush.verticalGradient(
                    listOf(Emerald, EmeraldDark),
                    startY = top,
                    endY   = chartH
                ),
                topLeft     = Offset(left, top.coerceAtLeast(0f)),
                size        = Size(barWidth, barHeight.coerceAtLeast(4f)),
                cornerRadius = CornerRadius(6f, 6f)
            )
        }
    }
}
