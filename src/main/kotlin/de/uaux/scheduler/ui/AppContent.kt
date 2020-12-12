package de.uaux.scheduler.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.ui.screens.EventManagementScreen
import de.uaux.scheduler.ui.screens.HomeScreen
import de.uaux.scheduler.ui.screens.SettingsScreen
import de.uaux.scheduler.ui.screens.TimetableScreen
import org.koin.androidx.compose.get

@Composable
fun AppContent() {
    MaterialTheme {
        Row {
            val navigationController: NavigationController = get()
            Sidebar(
                screenState = navigationController.currentScreen,
            )
            when (navigationController.currentScreen.value) {
                NavigationController.Screen.Home -> HomeScreen()
                NavigationController.Screen.Timetable -> TimetableScreen()
                NavigationController.Screen.Events -> EventManagementScreen()
                NavigationController.Screen.Settings -> SettingsScreen()
            }
        }
    }
}