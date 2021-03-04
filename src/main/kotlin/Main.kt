@file:JvmName("scheduler")

import androidx.compose.desktop.Window
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntSize
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

    Window(
        title = koin.get<LocalizationUtil>()["app_name"],
        size = IntSize(1600, 900),
    ) {
        val initialized by startupController.initialize()
        if (initialized) {
            AppContent()
        }
    }
}