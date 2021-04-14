package de.uaux.scheduler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
            for (screen in NavigationController.Screen.values()) {
                StatefulSidebarButton(
                    screenState = screenState,
                    screen = screen,
                    icon = screen.icon,
                )
            }
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