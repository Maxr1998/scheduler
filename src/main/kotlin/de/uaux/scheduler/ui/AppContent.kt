@file:Suppress("MagicNumber")

package de.uaux.scheduler.ui

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.ui.screens.EventManagementScreen
import de.uaux.scheduler.ui.screens.HomeScreen
import de.uaux.scheduler.ui.screens.SettingsScreen
import de.uaux.scheduler.ui.screens.StudycourseManagementScreen
import de.uaux.scheduler.ui.screens.TimetableScreen
import org.koin.androidx.compose.get

val LightColors = lightColors(
    primary = Color(0xFFFF9800),
    primaryVariant = Color(0xFFC66900),
    secondary = Color(0xFFFF9800),
    secondaryVariant = Color(0xFFC66900),
    background = Color(0xFFFAFAFA),
)

@Suppress("unused")
val MonochromeColors = lightColors(
    primary = Color(0xFF808080),
    primaryVariant = Color(0xFF606060),
    secondary = Color(0xFF808080),
    secondaryVariant = Color(0xFF606060),
    background = Color(0xFFFAFAFA),
    onPrimary = Color.White,
)

val DarkColors = darkColors(
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

@Composable
fun AppContent(darkTheme: Boolean = false) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
    ) {
        var scrollbarStyle = defaultScrollbarStyle()
        if (darkTheme) {
            val scrollbarColor = with(MaterialTheme.colors) {
                onSurface.copy(alpha = 0.2f).compositeOver(surface)
            }
            scrollbarStyle = scrollbarStyle.copy(hoverColor = scrollbarColor, unhoverColor = scrollbarColor)
        }
        CompositionLocalProvider(
            LocalScrollbarStyle provides scrollbarStyle,
        ) {
            Box {
                Row {
                    val navigationController: NavigationController = get()
                    Sidebar(
                        screenState = navigationController.currentScreen,
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background,
                    ) {
                        when (navigationController.currentScreen.value) {
                            NavigationController.Screen.Home -> HomeScreen()
                            NavigationController.Screen.Timetable -> TimetableScreen()
                            NavigationController.Screen.Events -> EventManagementScreen()
                            NavigationController.Screen.Studycourses -> StudycourseManagementScreen()
                            NavigationController.Screen.Settings -> SettingsScreen()
                        }
                    }
                }

                Dialogs()
            }
        }
    }
}