package de.uaux.scheduler.controller

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

class NavigationController {
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.Home)

    enum class Screen(val icon: ImageVector) {
        Home(Icons.Outlined.Home),
        Timetable(Icons.Outlined.DateRange),
        Events(Icons.Outlined.List),
        Settings(Icons.Outlined.Settings),
    }
}