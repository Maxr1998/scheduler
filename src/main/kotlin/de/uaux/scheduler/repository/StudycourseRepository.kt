package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.util.changedOne
import de.uaux.scheduler.util.checkAndGetId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import de.uaux.scheduler.model.StudycourseEvent as DbStudycourseEvent

class StudycourseRepository(database: Database) {
    private val standardQueries = database.standardQueries
    private val studycourseQueries = database.studycourseQueries
    private val studycourseEventQueries = database.studycourseEventQueries

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

    /**
     * Links the [Event] in [studycourseEvent] to [studycourse], replacing all attributes with the provided ones
     */
    suspend fun link(studycourse: Studycourse, studycourseEvent: StudycourseEvent): Boolean = withContext(Dispatchers.IO) {
        val relationObject = DbStudycourseEvent(studycourse.id, studycourseEvent.event.id, studycourseEvent.semester, studycourseEvent.required)
        studycourseEventQueries.insertOrReplace(relationObject)
        standardQueries.changedOne()
    }

    /**
     * Unlinks [event] from [studycourse], removing all attached metadata
     */
    suspend fun unlink(studycourse: Studycourse, event: Event) {
        withContext(Dispatchers.IO) {
            studycourseEventQueries.delete(studycourse.id, event.id)
        }
    }
}