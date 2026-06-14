package com.dsmp.pvpclient.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Custom DSMP dark colour scheme ────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary            = Emerald,
    onPrimary          = CosmicBlack,
    primaryContainer   = EmeraldDeep,
    onPrimaryContainer = EmeraldBright,

    secondary            = AccentBlue,
    onSecondary          = CosmicBlack,
    secondaryContainer   = Color(0xFF1A2A44),
    onSecondaryContainer = AccentCyan,

    tertiary            = AccentPurple,
    onTertiary          = CosmicBlack,
    tertiaryContainer   = Color(0xFF1A0033),
    onTertiaryContainer = Color(0xFFCC88FF),

    background          = SurfaceDeep,
    onBackground        = OnSurfacePrimary,

    surface             = SurfaceDark,
    onSurface           = OnSurfacePrimary,
    surfaceVariant      = SurfaceElevated,
    onSurfaceVariant    = OnSurfaceSecondary,

    surfaceContainer        = SurfaceCard,
    surfaceContainerHigh    = SurfaceElevated,
    surfaceContainerHighest = SurfaceHighlight,

    outline           = BorderDark,
    outlineVariant    = DividerDark,

    error             = Error,
    onError           = CosmicBlack,
    errorContainer    = Color(0xFF3A0000),
    onErrorContainer  = Color(0xFFFF9999),

    scrim             = ScrimDark,
    inverseSurface    = OnSurfacePrimary,
    inverseOnSurface  = SurfaceDeep,
    inversePrimary    = EmeraldDeep
)

/** Holds whether AMOLED pure-black mode is active. */
data class DSMPThemeConfig(
    val amoledMode: Boolean = false,
    val accentColor: Color  = Emerald
)

val LocalDSMPThemeConfig = staticCompositionLocalOf { DSMPThemeConfig() }

@Composable
fun DSMPTheme(
    amoledMode: Boolean = false,
    accentColor: Color  = Emerald,
    content: @Composable () -> Unit
) {
    val colorScheme = if (amoledMode) {
        DarkColorScheme.copy(
            background = CosmicBlack,
            surface    = Color(0xFF080808)
        )
    } else {
        DarkColorScheme
    }

    CompositionLocalProvider(
        LocalDSMPThemeConfig provides DSMPThemeConfig(amoledMode, accentColor)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = DSMPTypography,
            shapes      = DSMPShapes,
            content     = content
        )
    }
}
