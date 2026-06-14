package com.dsmp.pvpclient.domain.model

/**
 * Categories for resource packs.
 * Each category has a human-readable display name and an emoji icon used in the UI.
 */
enum class PackCategory(val displayName: String, val emoji: String) {
    PVP("PvP", "⚔️"),
    FPS("FPS Boost", "🚀"),
    SHORT_SWORDS("Short Swords", "🗡️"),
    LOW_FIRE("Low Fire", "🔥"),
    SOUND("Sound Packs", "🔊"),
    GUI("GUI Packs", "🖼️"),
    SKY("Sky Packs", "🌤️"),
    CUSTOM("Custom", "📦");

    companion object {
        fun fromName(name: String): PackCategory =
            entries.firstOrNull { it.name == name } ?: CUSTOM
    }
}
