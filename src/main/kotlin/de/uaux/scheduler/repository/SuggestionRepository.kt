package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.util.SuggestionParser
import de.uaux.scheduler.util.changedOne
import de.uaux.scheduler.util.checkAndGetId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SuggestionRepository(
    database: Database,
    private val suggestionParser: SuggestionParser,
) {
    private val standardQueries = database.standardQueries
    private val suggestionQueries = database.suggestionQueries
    private val constraintQueries = database.suggestionConstraintQueries

    suspend fun insertOrUpdate(suggestion: Suggestion): Long = withContext(Dispatchers.IO) {
        suggestionQueries.transactionWithResult {
            val id = run {
                if (suggestion.id > 0) {
                    // Existing object, attempt to update
                    // Only update text; event and semester are locked to the id!
                    suggestionQueries.update(suggestion.text, suggestion.id)
                    if (standardQueries.changedOne()) {
                        return@run suggestion.id
                    }
                }

                // Failed update or new object, insert
                suggestionQueries.insert(suggestion.semester.code, suggestion.event.id, suggestion.text)
                standardQueries.checkAndGetId()
            }

            // Update constraints
            if (id > 0) {
                constraintQueries.clearConstraintsBySuggestion(id)

                for (constraint in suggestion.constraints) {
                    constraintQueries.insert(constraint.persist())
                }
            }

            id
        }
    }

    fun querySuggestionBySemesterAndEvent(semester: Semester, event: Event): Suggestion? =
        suggestionQueries.querySuggestionBySemesterAndEvent(semester.code, event.id) { id, eventId, name, module, duration, participants, text ->
            val constraints = constraintQueries.querySuggestionConstraintsBySuggestion(id, suggestionParser::parseConstraint).executeAsList()
            Suggestion(id, semester, Event(eventId, name, module, duration, participants), text, constraints)
        }.executeAsOneOrNull()

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