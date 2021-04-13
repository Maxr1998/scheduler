package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.util.changedOne
import de.uaux.scheduler.util.checkAndGetId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EventRepository(
    database: Database,
) {
    private val standardQueries = database.standardQueries
    private val eventQueries = database.eventQueries

    val allEventsFlow: Flow<List<Event>> =
        eventQueries
            .queryAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    val eventCountFlow: Flow<Long> =
        eventQueries
            .queryEventCount()
            .asFlow()
            .mapToOne(Dispatchers.IO)

    suspend fun insertOrUpdate(event: Event): Long = withContext(Dispatchers.IO) {
        eventQueries.transactionWithResult {
            if (event.id > 0) {
                // Existing object, attempt to update
                eventQueries.update(event.name, event.module, event.participants, event.id)
                if (standardQueries.changedOne()) {
                    return@transactionWithResult event.id
                }
            }

            // Failed update or new object, insert
            eventQueries.insert(event.name, event.module, event.participants)
            standardQueries.checkAndGetId()
        }
    }

    suspend fun searchByName(query: String, exclude: Studycourse? = null): List<Event> = withContext(Dispatchers.IO) {
        when {
            query.isBlank() -> {
                if (exclude != null) eventQueries.queryAllNotInStudycourse(exclude.id)
                else eventQueries.queryAll()
            }
            else -> {
                val queryString = "%${query.trim()}%"
                if (exclude != null) eventQueries.queryAllNotInStudycourseByName(exclude.id, queryString)
                else eventQueries.queryAllByName(queryString)
            }
        }.executeAsList()
    }

    fun queryAllInStudycourseAsFlow(studycourse: Studycourse): Flow<List<StudycourseEvent>> =
        eventQueries
            .queryAllInStudycourse(studycourse.id) { id, name, module, participants, semester, required ->
                StudycourseEvent(Event(id, name, module, participants), semester, required)
            }
            .asFlow()
            .mapToList(Dispatchers.IO)
}