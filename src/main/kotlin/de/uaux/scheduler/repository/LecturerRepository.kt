package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Lecturer
import de.uaux.scheduler.util.changedOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LecturerRepository(
    database: Database,
) {
    private val standardQueries = database.standardQueries
    private val lecturerQueries = database.lecturerQueries
    private val eventLecturerQueries = database.eventLecturerQueries

    val allLecturersFlow: Flow<List<Lecturer>> =
        lecturerQueries
            .queryAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    suspend fun insert(name: String) {
        withContext(Dispatchers.IO) {
            lecturerQueries.insert(name)
        }
    }

    fun queryLecturersByEventAsFlow(event: Event): Flow<List<Lecturer>> =
        lecturerQueries.queryLecturersByEvent(event.id)
            .asFlow()
            .mapToList(Dispatchers.IO)

    suspend fun searchByName(query: String, exclude: Event? = null): List<Lecturer> = withContext(Dispatchers.IO) {
        when {
            query.isBlank() -> {
                if (exclude != null) lecturerQueries.queryAllNotInEvent(exclude.id)
                else lecturerQueries.queryAll()
            }
            else -> {
                val queryString = "%${query.trim()}%"
                if (exclude != null) lecturerQueries.queryAllNotInEventByName(exclude.id, queryString)
                else lecturerQueries.queryAllByName(queryString)
            }
        }.executeAsList()
    }

    suspend fun addLecturerToEvent(lecturer: Lecturer, event: Event): Boolean = withContext(Dispatchers.IO) {
        eventLecturerQueries.insertOrReplace(event.id, lecturer.id)
        standardQueries.changedOne()
    }

    suspend fun removeLecturerFromEvent(lecturer: Lecturer, event: Event): Boolean = withContext(Dispatchers.IO) {
        eventLecturerQueries.delete(event.id, lecturer.id)
        standardQueries.changedOne()
    }
}