import androidx.compose.desktop.Window
import androidx.compose.runtime.Providers
import androidx.compose.ui.unit.IntSize
import de.uaux.scheduler.appModule
import de.uaux.scheduler.ui.AppContent
import de.uaux.scheduler.util.AmbientLocalization
import de.uaux.scheduler.util.LocalizationUtil
import org.koin.core.context.startKoin

fun main() {
    val koinApp = startKoin {
        modules(appModule)
    }

    val localizationUtil: LocalizationUtil = koinApp.koin.get()

    Window(
        title = localizationUtil["app_name"],
        size = IntSize(1200, 820),
    ) {
        Providers(AmbientLocalization provides localizationUtil) {
            AppContent()
        }
    }
}