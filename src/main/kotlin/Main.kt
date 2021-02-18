@file:JvmName("scheduler")

import androidx.compose.desktop.Window
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntSize
import de.uaux.scheduler.appModule
import de.uaux.scheduler.controller.StartupController
import de.uaux.scheduler.ui.AppContent
import de.uaux.scheduler.util.AmbientLocalization
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
    val localizationUtil: LocalizationUtil = koin.get()

    Window(
        title = localizationUtil["app_name"],
        size = IntSize(1200, 820),
    ) {
        val initialized by startupController.initialize()
        if (initialized) {
            Providers(AmbientLocalization provides localizationUtil) {
                AppContent()
            }
        }
    }
}