package com.dsmp.pvpclient.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dsmp.pvpclient.ui.navigation.Screen
import com.dsmp.pvpclient.ui.theme.SurfaceCard

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem(Screen.Home,          "Home",     Icons.Rounded.Home),
    NavItem(Screen.ResourcePacks, "Packs",    Icons.Rounded.Layers),
    NavItem(Screen.Profiles,      "Profiles", Icons.Rounded.Person),
    NavItem(Screen.Statistics,    "Stats",    Icons.Rounded.BarChart),
    NavItem(Screen.Settings,      "Settings", Icons.Rounded.Settings),
)

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier         = modifier,
        containerColor   = SurfaceCard,
        tonalElevation   = 0.dp
    ) {
        navItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            val iconTint by animateColorAsState(
                targetValue    = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec  = tween(200),
                label          = "navIconTint"
            )

            NavigationBarItem(
                selected = selected,
                onClick  = {
                    if (!selected) {
                        navController.navigate(item.screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon  = {
                    Icon(
                        imageVector      = item.icon,
                        contentDescription = item.label,
                        tint             = iconTint,
                        modifier         = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text  = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = iconTint
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}
