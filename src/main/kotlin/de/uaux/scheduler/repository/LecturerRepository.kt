package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Lecturer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LecturerRepository(
    database: Database,
) {
    private val lecturerQueries = database.lecturerQueries

    val allLecturersFlow: Flow<List<Lecturer>> =
        lecturerQueries
            .queryAll()
            .asFlow()
            .mapToList(Dispatchers.IO)

    suspend fun queryLecturersByEvent(event: Event): List<Lecturer> = withContext(Dispatchers.IO) {
        lecturerQueries.queryLecturersByEvent(event.id).executeAsList()
    }
}