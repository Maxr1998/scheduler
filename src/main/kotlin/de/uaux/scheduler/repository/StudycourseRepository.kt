package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.util.checkAndGetId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudycourseRepository(database: Database) {
    private val standardQueries = database.standardQueries
    private val studycourseQueries = database.studycourseQueries

    val allStudycoursesFlow: Flow<List<Studycourse>> =
        studycourseQueries
            .queryAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    suspend fun insert(name: String, revision: String? = null): Long {
        return withContext(Dispatchers.IO) {
            studycourseQueries.transactionWithResult {
                studycourseQueries.insert(name, revision)
                standardQueries.checkAndGetId()
            }
        }
    }

    suspend fun queryAll(): List<Studycourse> {
        return withContext(Dispatchers.IO) {
            studycourseQueries.queryAll().executeAsList()
        }
    }
}