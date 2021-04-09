package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.util.changedOne
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

    suspend fun insertOrUpdate(studycourse: Studycourse): Long = withContext(Dispatchers.IO) {
        studycourseQueries.transactionWithResult {
            if (studycourse.id > 0) {
                // Existing object, attempt to update
                studycourseQueries.update(studycourse.name, studycourse.revision, studycourse.id)
                if (standardQueries.changedOne()) {
                    return@transactionWithResult studycourse.id
                }
            }

            // Failed update or new object, insert
            studycourseQueries.insert(studycourse.name, studycourse.revision)
            standardQueries.checkAndGetId()
        }
    }
}