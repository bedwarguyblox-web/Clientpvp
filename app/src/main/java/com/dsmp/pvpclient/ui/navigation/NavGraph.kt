package com.dsmp.pvpclient.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dsmp.pvpclient.ui.components.BottomNavBar
import com.dsmp.pvpclient.ui.screens.home.HomeScreen
import com.dsmp.pvpclient.ui.screens.hud.HudEditorScreen
import com.dsmp.pvpclient.ui.screens.performance.PerformanceScreen
import com.dsmp.pvpclient.ui.screens.profiles.ProfilesScreen
import com.dsmp.pvpclient.ui.screens.resourcepacks.ResourcePacksScreen
import com.dsmp.pvpclient.ui.screens.settings.SettingsScreen
import com.dsmp.pvpclient.ui.screens.splash.SplashScreen
import com.dsmp.pvpclient.ui.screens.statistics.StatisticsScreen

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.ResourcePacks.route,
    Screen.Profiles.route,
    Screen.Statistics.route,
    Screen.Settings.route
)

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = Screen.Splash.route,
            modifier         = Modifier.padding(innerPadding),
            enterTransition  = {
                fadeIn(tween(200)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(200)
                )
            },
            exitTransition   = {
                fadeOut(tween(150)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start, tween(150)
                )
            },
            popEnterTransition = {
                fadeIn(tween(200)) + slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(200)
                )
            },
            popExitTransition  = {
                fadeOut(tween(150)) + slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End, tween(150)
                )
            }
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToPerformance  = { navController.navigate(Screen.Performance.route) },
                    onNavigateToHud          = { navController.navigate(Screen.HudEditor.route) },
                    onNavigateToPacks        = { navController.navigate(Screen.ResourcePacks.route) },
                    onNavigateToProfiles     = { navController.navigate(Screen.Profiles.route) }
                )
            }

            composable(Screen.ResourcePacks.route) {
                ResourcePacksScreen()
            }

            composable(Screen.Profiles.route) {
                ProfilesScreen()
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToPerformance = { navController.navigate(Screen.Performance.route) },
                    onNavigateToHud         = { navController.navigate(Screen.HudEditor.route) },
                    onNavigateToAppearance  = { navController.navigate(Screen.Appearance.route) }
                )
            }

            composable(Screen.Performance.route) {
                PerformanceScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.HudEditor.route) {
                HudEditorScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Appearance.route) {
                // Appearance is a sub-page of Settings — reuses SettingsScreen tab in future
                SettingsScreen(
                    onNavigateToPerformance = { navController.navigate(Screen.Performance.route) },
                    onNavigateToHud         = { navController.navigate(Screen.HudEditor.route) },
                    onNavigateToAppearance  = { navController.navigate(Screen.Appearance.route) }
                )
            }
        }
    }
}
