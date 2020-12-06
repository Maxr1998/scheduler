package de.uaux.scheduler.controller

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class NavigationController {
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.Home)

    sealed class Screen {
        object Home : Screen()
        object Timetable : Screen()
        object Events : Screen()
        object Settings : Screen()
    }
}