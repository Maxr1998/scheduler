package de.uaux.scheduler

import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.util.LocalizationUtil
import org.koin.dsl.module

val appModule = module {
    single { NavigationController() }

    single { LocalizationUtil("strings") }
}