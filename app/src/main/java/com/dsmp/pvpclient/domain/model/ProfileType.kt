package com.dsmp.pvpclient.domain.model

/**
 * The type / play-style a profile is optimised for.
 */
enum class ProfileType(val displayName: String, val emoji: String, val accentHex: String) {
    CRYSTAL_PVP("Crystal PvP",  "💎", "#B0FFFF"),
    LIFESTEAL  ("Lifesteal",    "❤️",  "#FF4444"),
    SMP        ("SMP",          "🏡", "#44FF88"),
    BEDWARS    ("Bedwars",      "🛏️",  "#4488FF"),
    SKYWARS    ("Skywars",      "🌤️",  "#88AAFF"),
    KIT_PVP    ("KitPvP",       "⚔️",  "#FFD600"),
    PRACTICE   ("Practice",     "🎯",  "#FF8800"),
    CUSTOM     ("Custom",       "⚙️",  "#AAAAAA");

    companion object {
        fun fromName(name: String): ProfileType =
            entries.firstOrNull { it.name == name } ?: CUSTOM
    }
}
