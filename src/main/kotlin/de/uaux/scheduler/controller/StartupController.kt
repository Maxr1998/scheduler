package de.uaux.scheduler.controller

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.squareup.sqldelight.db.SqlDriver
import de.uaux.scheduler.model.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KLoggable

class StartupController(
    private val sqlDriver: SqlDriver,
) : KLoggable {
    override val logger = logger()
    private val coroutineScope = MainScope()

    private val startupState: MutableState<Boolean> = mutableStateOf(false)
    private var startupJob: Job? = null

    private var databaseVersion: Int
        get() = sqlDriver
            .executeQuery(null, "PRAGMA user_version;", 0, null)
            .getLong(0)!!
            .toInt()
        set(version) {
            sqlDriver.execute(null, "PRAGMA user_version = $version;", 0, null)
        }

    @Synchronized
    fun initialize(): State<Boolean> {
        if (!startupState.value && startupJob?.isActive != true) {
            startupJob = coroutineScope.launch {
                logger.debug("Running startup procedure…")
                withContext(Dispatchers.IO) {
                    val databaseVer = databaseVersion
                    val schemaVer = Database.Schema.version
                    when {
                        databaseVer == 0 -> {
                            Database.Schema.create(sqlDriver)
                            databaseVersion = 1
                            logger.info("Created initial database schema")
                        }
                        schemaVer > databaseVer -> {
                            Database.Schema.migrate(sqlDriver, databaseVer, schemaVer)
                            databaseVersion = schemaVer
                            logger.info("Migrated database from version $databaseVer to $schemaVer")
                        }
                        else -> logger.info("Initialized database")
                    }
                }
                startupState.value = true
                logger.info("Startup procedure completed successfully")
            }
        }
        return startupState
    }
}