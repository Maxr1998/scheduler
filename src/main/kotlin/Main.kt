import androidx.compose.desktop.Window
import de.uaux.scheduler.appModule
import de.uaux.scheduler.ui.AppContent
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }

    Window {
        AppContent()
    }
}