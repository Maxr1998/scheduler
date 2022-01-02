package de.uaux.scheduler.util

import java.util.MissingResourceException
import java.util.ResourceBundle

class LocalizationUtil(bundleName: String) {
    private val bundle = ResourceBundle.getBundle(bundleName)

    operator fun get(key: String): String = try {
        bundle.getString(key)
    } catch (e: MissingResourceException) {
        key
    }
}