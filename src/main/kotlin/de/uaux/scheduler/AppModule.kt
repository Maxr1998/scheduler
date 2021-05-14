package de.uaux.scheduler

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.controller.StartupController
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.LecturerRepository
import de.uaux.scheduler.repository.ScheduleRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.repository.SuggestionRepository
import de.uaux.scheduler.util.Constants
import de.uaux.scheduler.util.LocalizationUtil
import de.uaux.scheduler.util.SuggestionParser
import de.uaux.scheduler.viewmodel.DialogViewModel
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import de.uaux.scheduler.viewmodel.HomeViewModel
import de.uaux.scheduler.viewmodel.TimetableViewModel
import org.koin.dsl.module

val appModule = module {
    // Controllers
    single { StartupController(get()) }
    single { NavigationController() }

    // Viewmodels
    single { DialogViewModel() }
    single { HomeViewModel(get(), get(), get(), get()) }
    single { EventManagementViewModel(get(), get()) }
    single { TimetableViewModel(get(), get(), get()) }

    // Repositories
    single { StudycourseRepository(get()) }
    single { EventRepository(get()) }
    single { LecturerRepository(get()) }
    single { ScheduleRepository(get(), get()) }
    single { SuggestionRepository(get(), get()) }

    // Database
    single<SqlDriver> { JdbcSqliteDriver(Constants.DB_URL) }
    single { Database(get()) }

    // Utils
    single { LocalizationUtil("strings") }
    single { SuggestionParser() }
}