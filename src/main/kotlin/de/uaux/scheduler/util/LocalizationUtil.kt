package de.uaux.scheduler.util

import androidx.compose.runtime.staticAmbientOf
import java.util.*

class LocalizationUtil(bundleName: String) {
    private val bundle = ResourceBundle.getBundle(bundleName)

    operator fun get(key: String): String = try {
        bundle.getString(key)
    } catch (e: MissingResourceException) {
        key
    }
}

val AmbientLocalization = staticAmbientOf<LocalizationUtil>()