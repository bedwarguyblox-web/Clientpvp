package com.dsmp.pvpclient.domain.model

/**
 * A launcher profile linking a [ProfileType] to a specific resource pack and performance preset.
 */
data class Profile(
    val id: Long = 0,
    val name: String,
    val type: ProfileType,
    val activePackId: Long? = null,
    val performanceMode: PerformanceMode = PerformanceMode.BALANCED,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsed: Long = System.currentTimeMillis(),
    val iconColorHex: String = "#00E676"
) {
    val emoji: String get() = type.emoji
}
