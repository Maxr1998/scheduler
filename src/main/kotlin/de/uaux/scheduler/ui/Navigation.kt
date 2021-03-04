package de.uaux.scheduler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.ui.util.ZIndex

@Composable
fun Sidebar(
    screenState: MutableState<NavigationController.Screen>
) {
    Surface(
        modifier = Modifier.width(64.dp).fillMaxHeight().zIndex(ZIndex.NAVIGATION),
        elevation = 1.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StatefulSidebarButton(
                screenState = screenState,
                screen = NavigationController.Screen.Home,
                icon = Icons.Outlined.Home,
            )
            StatefulSidebarButton(
                screenState = screenState,
                screen = NavigationController.Screen.Timetable,
                icon = Icons.Outlined.DateRange,
            )
            StatefulSidebarButton(
                screenState = screenState,
                screen = NavigationController.Screen.Events,
                icon = Icons.Outlined.List,
            )
            StatefulSidebarButton(
                screenState = screenState,
                screen = NavigationController.Screen.Settings,
                icon = Icons.Outlined.Settings,
            )
        }
    }
}

@Composable
private fun StatefulSidebarButton(
    screenState: MutableState<NavigationController.Screen>,
    screen: NavigationController.Screen,
    icon: ImageVector,
) {
    var currentScreen by screenState
    SidebarButton(
        onClick = { currentScreen = screen },
        icon = icon,
        active = currentScreen == screen
    )
}

@Composable
private fun SidebarButton(
    onClick: () -> Unit,
    icon: ImageVector,
    active: Boolean
) {
    IconButton(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            tint = if (active) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
        )
    }
}