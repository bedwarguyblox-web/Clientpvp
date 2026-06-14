package com.dsmp.pvpclient.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsmp.pvpclient.ui.theme.CosmicBlack
import com.dsmp.pvpclient.ui.theme.Emerald
import com.dsmp.pvpclient.ui.theme.EmeraldBright
import com.dsmp.pvpclient.ui.theme.OnSurfaceSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    // ── Animation states ───────────────────────────────────────────────────
    val iconAlpha     = remember { Animatable(0f) }
    val iconScale     = remember { Animatable(0.6f) }
    val titleAlpha    = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Icon pop
        iconAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        iconScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        delay(100)
        // Title
        titleAlpha.animateTo(1f, tween(400))
        delay(150)
        // Tagline
        subtitleAlpha.animateTo(1f, tween(350))
        // Hold then navigate
        delay(1000)
        onFinished()
    }

    Box(
        modifier           = Modifier
            .fillMaxSize()
            .background(CosmicBlack),
        contentAlignment   = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Shield icon ───────────────────────────────────────────────
            Icon(
                imageVector        = Icons.Rounded.Shield,
                contentDescription = null,
                tint               = Emerald,
                modifier           = Modifier
                    .size(80.dp)
                    .alpha(iconAlpha.value)
                    .scale(iconScale.value)
            )

            Spacer(Modifier.height(24.dp))

            // ── App name ──────────────────────────────────────────────────
            Text(
                text       = "DSMP",
                style      = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color      = Emerald,
                modifier   = Modifier.alpha(titleAlpha.value)
            )
            Text(
                text       = "PvP Client",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = EmeraldBright,
                modifier   = Modifier.alpha(titleAlpha.value)
            )

            Spacer(Modifier.height(12.dp))

            // ── Tagline ───────────────────────────────────────────────────
            Text(
                text      = "The Ultimate Bedrock PvP Launcher",
                style     = MaterialTheme.typography.bodyMedium,
                color     = OnSurfaceSecondary,
                textAlign = TextAlign.Center,
                modifier  = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(Modifier.height(48.dp))

            // ── Loading indicator ─────────────────────────────────────────
            CircularProgressIndicator(
                color    = Emerald.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .alpha(subtitleAlpha.value),
                strokeWidth = 2.dp
            )
        }
    }
}
