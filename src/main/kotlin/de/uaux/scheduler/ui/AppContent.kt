package de.uaux.scheduler.ui

import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.ui.screens.EventManagementScreen
import de.uaux.scheduler.ui.screens.HomeScreen
import de.uaux.scheduler.ui.screens.SettingsScreen
import de.uaux.scheduler.ui.screens.TimetableScreen
import org.koin.androidx.compose.get

@Composable
fun AppContent() {
    MaterialTheme(
        colors = darkColors(
            primary = Color(0xFFFF9800),
            primaryVariant = Color(0xFFC66900),
            secondary = Color(0xFFBF360C),
            background = Color(0xFF121212),
            surface = Color(0xFF121212),
            error = Color(0xFFDD2C00),
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
        )
    ) {
        val scrollbarColor = with(MaterialTheme.colors) {
            onSurface.copy(alpha = 0.2f).compositeOver(surface)
        }
        Providers(
            ScrollbarStyleAmbient provides defaultScrollbarStyle().copy(
                hoverColor = scrollbarColor,
                unhoverColor = scrollbarColor,
            ),
        ) {
            Row {
                val navigationController: NavigationController = get()
                Sidebar(
                    screenState = navigationController.currentScreen,
                )
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (navigationController.currentScreen.value) {
                        NavigationController.Screen.Home -> HomeScreen()
                        NavigationController.Screen.Events -> EventManagementScreen()
                        NavigationController.Screen.Timetable -> TimetableScreen()
                        NavigationController.Screen.Settings -> SettingsScreen()
                    }
                }
            }
        }
    }
}