package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.util.SuggestionParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class EventRepository(
    database: Database,
    private val suggestionParser: SuggestionParser,
) {
    private val eventQueries = database.eventQueries
    private val suggestionQueries = database.suggestionQueries
    private val constraintQueries = database.suggestionConstraintQueries

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

    fun queryEventSuggestions(studycourse: Studycourse): List<Suggestion> =
        suggestionQueries.queryAllSuggestionsInStudycourse(studycourse.id) { id, eventId, name, module, participants, duration, text ->
            val constraints = constraintQueries.queryAllSuggestionConstraints(id, suggestionParser::parseConstraint).executeAsList()
            Suggestion(id, Event(eventId, name, module, participants), duration, text, constraints)
        }.executeAsList()
}