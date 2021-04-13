package de.uaux.scheduler.repository

import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.util.SuggestionParser

class SuggestionRepository(
    database: Database,
    private val suggestionParser: SuggestionParser,
) {
    private val suggestionQueries = database.suggestionQueries
    private val constraintQueries = database.suggestionConstraintQueries

    fun querySuggestions(studycourse: Studycourse, semester: Semester): List<Suggestion> =
        suggestionQueries.querySuggestionsInStudycourseBySemester(studycourse.id, semester.code) { id, eventId, name, module, participants, duration, text ->
            val constraints = constraintQueries.querySuggestionConstraintsBySuggestion(id, suggestionParser::parseConstraint).executeAsList()
            Suggestion(id, semester, Event(eventId, name, module, participants), duration, text, constraints)
        }.executeAsList()
}