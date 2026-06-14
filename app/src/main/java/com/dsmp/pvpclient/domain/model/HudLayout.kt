package com.dsmp.pvpclient.domain.model

/**
 * A saved HUD layout: a named set of [HudElement] positions.
 */
data class HudLayout(
    val id: Long = 0,
    val name: String,
    val elements: List<HudElement>,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        /** Returns the default layout every new user starts with. */
        fun default(): HudLayout = HudLayout(
            id = 0,
            name = "Default",
            elements = listOf(
                HudElement("fps",     HudElementType.FPS_COUNTER,   x = 8f,  y = 8f),
                HudElement("cps",     HudElementType.CPS_COUNTER,   x = 8f,  y = 32f),
                HudElement("coords",  HudElementType.COORDINATES,   x = 8f,  y = 56f),
                HudElement("ping",    HudElementType.PING,           x = 220f, y = 8f),
                HudElement("clock",   HudElementType.CLOCK,          x = 220f, y = 32f),
                HudElement("combo",   HudElementType.COMBO_COUNTER,  x = 8f,  y = 80f,  isVisible = false),
                HudElement("dir",     HudElementType.DIRECTION,      x = 120f, y = 8f,   isVisible = false),
                HudElement("session", HudElementType.SESSION_TIMER,  x = 220f, y = 56f,  isVisible = false)
            ),
            isActive = true
        )
    }
}
