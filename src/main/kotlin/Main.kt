@file:JvmName("scheduler")

import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.uaux.scheduler.appModule
import de.uaux.scheduler.controller.StartupController
import de.uaux.scheduler.ui.AppContent
import de.uaux.scheduler.util.LocalizationUtil
import mu.KotlinLogging
import org.koin.core.context.startKoin

private val logger = KotlinLogging.logger {}

fun main() {
    val koinApp = startKoin {
        modules(appModule)
    }
    val koin = koinApp.koin
    logger.debug("Koin module initialized")

    val startupController: StartupController = koin.get()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = rememberWindowState(size = WindowSize(1600.dp, 900.dp)),
            title = koin.get<LocalizationUtil>()["app_name"],
        ) {
            val initialized by startupController.initialize()
            if (initialized) {
                AppContent()
            }
        }
    }
}