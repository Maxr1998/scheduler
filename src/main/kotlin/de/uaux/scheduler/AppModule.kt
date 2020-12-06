package de.uaux.scheduler

import de.uaux.scheduler.controller.NavigationController
import org.koin.dsl.module

val appModule = module {
    single { NavigationController() }
}