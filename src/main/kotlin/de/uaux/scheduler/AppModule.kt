package de.uaux.scheduler

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.util.Constants
import de.uaux.scheduler.util.LocalizationUtil
import org.koin.dsl.module

val appModule = module {
    // Controllers
    single { NavigationController() }

    // Database
    single<SqlDriver> { JdbcSqliteDriver(Constants.DB_URL) }
    single { Database(get()) }

    // Utils
    single { LocalizationUtil("strings") }
}