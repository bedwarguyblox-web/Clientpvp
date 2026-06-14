package com.dsmp.pvpclient.domain.model

/**
 * Performance presets that control how Minecraft is advised to run.
 * These values are stored in preferences and shown in the Performance Centre UI.
 */
enum class PerformanceMode(
    val displayName: String,
    val description: String,
    val emoji: String,
    val renderDistance: Int,
    val particlesEnabled: Boolean,
    val smoothLighting: Boolean,
    val fancyGraphics: Boolean
) {
    BATTERY_SAVER(
        displayName      = "Battery Saver",
        description      = "Minimum graphics, maximum battery life",
        emoji            = "🔋",
        renderDistance   = 4,
        particlesEnabled = false,
        smoothLighting   = false,
        fancyGraphics    = false
    ),
    BALANCED(
        displayName      = "Balanced",
        description      = "Good mix of visuals and performance",
        emoji            = "⚖️",
        renderDistance   = 8,
        particlesEnabled = true,
        smoothLighting   = false,
        fancyGraphics    = false
    ),
    PERFORMANCE(
        displayName      = "Performance",
        description      = "High FPS with clean visuals",
        emoji            = "⚡",
        renderDistance   = 6,
        particlesEnabled = false,
        smoothLighting   = false,
        fancyGraphics    = false
    ),
    ULTRA(
        displayName      = "Ultra Performance",
        description      = "Maximum FPS — no visual extras",
        emoji            = "🚀",
        renderDistance   = 4,
        particlesEnabled = false,
        smoothLighting   = false,
        fancyGraphics    = false
    );

    companion object {
        fun fromName(name: String): PerformanceMode =
            entries.firstOrNull { it.name == name } ?: BALANCED
    }
}
