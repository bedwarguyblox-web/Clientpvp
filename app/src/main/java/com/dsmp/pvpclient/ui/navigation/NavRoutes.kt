package com.dsmp.pvpclient.ui.navigation

/**
 * All navigation destinations in the DSMP PvP Client.
 * String routes are used with Compose Navigation 2.7.x.
 */
sealed class Screen(val route: String) {

    // ── Top-level ─────────────────────────────────────────────────────────
    object Splash       : Screen("splash")
    object Home         : Screen("home")
    object ResourcePacks : Screen("resource_packs")
    object Profiles     : Screen("profiles")
    object Statistics   : Screen("statistics")
    object Settings     : Screen("settings")

    // ── Secondary ─────────────────────────────────────────────────────────
    object Performance  : Screen("performance")
    object HudEditor    : Screen("hud_editor")
    object Appearance   : Screen("settings/appearance")

    // ── Detail screens (with args) ─────────────────────────────────────────
    object PackDetail : Screen("resource_packs/{packId}") {
        fun createRoute(packId: Long) = "resource_packs/$packId"
    }
    object ProfileDetail : Screen("profiles/{profileId}") {
        fun createRoute(profileId: Long) = "profiles/$profileId"
    }
}

/** Bottom navigation items — shown in the persistent bottom bar. */
enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconResName: String          // mapped to an Icons.* constant in BottomNavBar
) {
    HOME          (Screen.Home,          "Home",    "home"),
    RESOURCE_PACKS(Screen.ResourcePacks, "Packs",   "layers"),
    PROFILES      (Screen.Profiles,      "Profiles","person"),
    STATISTICS    (Screen.Statistics,    "Stats",   "bar_chart"),
    SETTINGS      (Screen.Settings,      "Settings","settings"),
}
