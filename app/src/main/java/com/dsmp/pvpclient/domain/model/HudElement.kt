package com.dsmp.pvpclient.domain.model

/**
 * Types of HUD widgets the user can position on screen.
 */
enum class HudElementType(val displayName: String, val defaultValue: String) {
    FPS_COUNTER    ("FPS",       "144"),
    CPS_COUNTER    ("CPS",       "12"),
    COORDINATES    ("Coords",    "0 / 64 / 0"),
    PING           ("Ping",      "28ms"),
    CLOCK          ("Clock",     "14:30"),
    COMBO_COUNTER  ("Combo",     "5x"),
    DIRECTION      ("Direction", "N"),
    SESSION_TIMER  ("Session",   "00:12:34")
}

/**
 * A single HUD element with its position, scale, opacity, and visibility.
 * Coordinates are in dp relative to the top-left of the screen.
 */
data class HudElement(
    val id: String,
    val type: HudElementType,
    val x: Float = 16f,
    val y: Float = 16f,
    val scale: Float = 1.0f,
    val opacity: Float = 1.0f,
    val isVisible: Boolean = true
)
