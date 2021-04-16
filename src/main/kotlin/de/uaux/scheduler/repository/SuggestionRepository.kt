package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.util.SuggestionParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SuggestionRepository(
    database: Database,
    private val suggestionParser: SuggestionParser,
) {
    private val suggestionQueries = database.suggestionQueries
    private val constraintQueries = database.suggestionConstraintQueries

    fun querySuggestions(studycourse: Studycourse, semester: Semester): List<Suggestion> =
        suggestionQueries.querySuggestionsInStudycourseBySemester(studycourse.id, semester.code) { id, eventId, name, module, duration, participants, text ->
            val constraints = constraintQueries.querySuggestionConstraintsBySuggestion(id, suggestionParser::parseConstraint).executeAsList()
            Suggestion(id, semester, Event(eventId, name, module, duration, participants), text, constraints)
        }.executeAsList()

    fun querySuggestionCountBySemesterAsFlow(semester: Semester): Flow<Long> =
        suggestionQueries
            .querySuggestionCountBySemester(semester.code)
            .asFlow()
            .mapToOne(Dispatchers.IO)

    fun queryUnprocessedSuggestionCountBySemesterAsFlow(semester: Semester): Flow<Long> =
        suggestionQueries
            .queryUnprocessedSuggestionCountBySemester(semester.code)
            .asFlow()
            .mapToOne(Dispatchers.IO)
}