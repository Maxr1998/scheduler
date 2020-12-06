package de.uaux.scheduler.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.controller.NavigationController
import org.koin.androidx.compose.get

@Composable
fun AppContent() {
    MaterialTheme {
        Row {
            val navigationController: NavigationController = get()
            Sidebar(
                screenState = navigationController.currentScreen,
            )
            Column(
                modifier = Modifier.padding(8.dp)
            ) {

            }
        }
    }
}