package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.EventSuggestion
import de.uaux.scheduler.model.ScheduledEvent
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.StudycourseEvent
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.util.SuggestionParser
import de.uaux.scheduler.util.executeAsMappedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek

class EventRepository(
    database: Database,
    private val suggestionParser: SuggestionParser,
) {
    private val eventQueries = database.eventQueries
    private val scheduleQueries = database.scheduleQueries
    private val timeslotQueries = database.timeslotQueries
    private val suggestionQueries = database.suggestionQueries
    private val constraintQueries = database.suggestionConstraintQueries

    val allSemestersFlow: Flow<List<Semester>> =
        scheduleQueries
            .queryAllSemesters()
            .asFlow()
            .map { query ->
                withContext(Dispatchers.IO) {
                    query.executeAsMappedList { semester -> Semester(semester) }
                }
            }

    fun queryAllInStudycourseAsFlow(studycourse: Studycourse): Flow<List<StudycourseEvent>> =
        eventQueries
            .queryAllInStudycourse(studycourse.id) { id, name, module, participants, semester, required ->
                StudycourseEvent(Event(id, name, module, participants), semester, required)
            }
            .asFlow()
            .mapToList(Dispatchers.IO)

    fun queryScheduledEvents(studycourse: Studycourse, semester: Semester, day: DayOfWeek): List<ScheduledEvent> =
        eventQueries.queryScheduledEvents(studycourse.id, semester.code, day.value) { id, name, module, participants, startTime, endTime, room ->
            ScheduledEvent(studycourse, Event(id, name, module, participants), day, startTime, endTime, room)
        }.executeAsList()

    fun queryTimeslots(semester: Semester): List<Timeslot> =
        timeslotQueries.queryTimeslotsForSemester(semester.code).executeAsList()

    fun queryEventSuggestions(studycourse: Studycourse): List<EventSuggestion> =
        suggestionQueries.queryAllSuggestionsInStudycourse(studycourse.id) { id, eventId, name, module, participants ->
            val constraints = constraintQueries.queryAllSuggestionConstraints(id, suggestionParser::parseConstraint).executeAsList()
            EventSuggestion(id, Event(eventId, name, module, participants), constraints)
        }.executeAsList()
}