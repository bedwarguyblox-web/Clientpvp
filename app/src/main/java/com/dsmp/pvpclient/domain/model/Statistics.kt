package com.dsmp.pvpclient.domain.model

/**
 * Aggregated statistics displayed on the Statistics screen.
 */
data class Statistics(
    val id: Long = 1,                      // Single row in Room (singleton pattern)
    val sessionsPlayed: Int = 0,
    val totalUsageMinutes: Long = 0L,
    val lastSessionMinutes: Int = 0,
    val favoriteProfileId: Long? = null,
    val favoritePackId: Long? = null,
    val lastLaunch: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalUsageFormatted: String
        get() {
            val hours   = totalUsageMinutes / 60
            val minutes = totalUsageMinutes % 60
            return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }

    val lastLaunchFormatted: String
        get() {
            if (lastLaunch == 0L) return "Never"
            val diff = System.currentTimeMillis() - lastLaunch
            val mins  = diff / 60_000
            val hours = mins / 60
            val days  = hours / 24
            return when {
                days  > 0 -> "${days}d ago"
                hours > 0 -> "${hours}h ago"
                mins  > 0 -> "${mins}m ago"
                else      -> "Just now"
            }
        }
}
