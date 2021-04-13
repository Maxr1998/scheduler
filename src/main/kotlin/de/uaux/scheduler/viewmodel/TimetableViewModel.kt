package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.repository.ScheduleRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.repository.SuggestionRepository
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.model.TimetableSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import java.time.DayOfWeek

private val logger = KotlinLogging.logger {}

class TimetableViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val scheduleRepository: ScheduleRepository,
    private val suggestionRepository: SuggestionRepository,
) {
    private val coroutineScope = MainScope()

    val studycoursesFlow: Flow<List<Studycourse>> get() = studycourseRepository.allStudycoursesFlow
    val semestersFlow: Flow<List<Semester>> get() = scheduleRepository.allSemestersFlow

    /**
     * The currently selected [Semester] and [Studycourse] that filters the events in the timetable
     */
    val timetableSelection: State<TimetableSelection> get() = _timetableSelection
    private val _timetableSelection: MutableState<TimetableSelection> = mutableStateOf(TimetableSelection.None)

    /**
     * Contains [Event]s for the currently selected schedule
     */
    val events: SnapshotStateList<ScheduledEvent> = mutableStateListOf()

    /**
     * Whether to show the weekend in the timetable interface
     */
    val showWeekend: MutableState<ShowWeekend> = mutableStateOf(ShowWeekend.FALSE)

    /**
     * Contains times for the start and end of the day so that all events in [events] are contained
     */
    val dayRange: State<IntRange> get() = _dayRange
    private val _dayRange: MutableState<IntRange> = mutableStateOf(TIMETABLE_DEFAULT_START_OF_DAY..TIMETABLE_DEFAULT_END_OF_DAY)

    /**
     * [Timeslot]s for the selected semester
     */
    val timeslots: State<List<Timeslot>> get() = _timeslots
    private val _timeslots: MutableState<List<Timeslot>> = mutableStateOf(emptyList())

    /**
     * Contains [Suggestion]s which are displayed in the timetable sidebar
     */
    val suggestions: SnapshotStateList<Suggestion> = SnapshotStateList()

    init {
        coroutineScope.launch {
            studycoursesFlow.collect { studycourses ->
                if (studycourses.isNotEmpty()) {
                    loadContent(scheduleRepository.computeNextSemester(), studycourses[0])
                    cancel()
                }
            }
        }
    }

    fun loadContent(semester: Semester, studycourse: Studycourse) = coroutineScope.launch {
        _timetableSelection.value = TimetableSelection.Loading

        val eventsJob = launch {
            // Query scheduled events
            val scheduledEvents = withContext(Dispatchers.IO) {
                scheduleRepository.queryScheduledEvents(studycourse, semester)
            }

            val hasWeekends = scheduledEvents.any { event -> event.day > DayOfWeek.FRIDAY }
            showWeekend.value = if (hasWeekends) ShowWeekend.FORCE else ShowWeekend.FALSE

            events.clear()
            events.addAll(scheduledEvents)
        }

        val timeslotsJob = launch {
            // Refresh timeslots
            val timeslots = withContext(Dispatchers.IO) {
                scheduleRepository.queryTimeslots(semester)
            }

            // Calculate min and max times for schedule
            val startOfDay = timeslots.minOfOrNull(Timeslot::start_time) ?: TIMETABLE_DEFAULT_START_OF_DAY
            val endOfDay = timeslots.maxOfOrNull(Timeslot::end_time) ?: TIMETABLE_DEFAULT_END_OF_DAY

            _timeslots.value = timeslots
            _dayRange.value = startOfDay..endOfDay
        }

        val suggestionsJob = launch {
            // Refresh suggestions
            val suggestions = withContext(Dispatchers.IO) {
                suggestionRepository.querySuggestions(studycourse, semester)
            }

            this@TimetableViewModel.suggestions.clear()
            this@TimetableViewModel.suggestions.addAll(suggestions)
        }

        joinAll(eventsJob, timeslotsJob, suggestionsJob)

        _timetableSelection.value = TimetableSelection.Loaded(semester, studycourse)
    }

    fun reschedule(event: ScheduledEvent, day: DayOfWeek, startTime: Int) = coroutineScope.launch {
        logger.debug { "Rescheduling $event to $day at $startTime" }

        // Get event index and check if present
        val index = events.indexOf(event)
        if (index < 0) {
            // Abort if invalid event
            logger.error { "$event not in list" }
            return@launch
        }

        // Calculate end time
        val endTime = startTime + event.duration

        // Apply changes to ViewModel
        val rescheduledEvent = event.copy(day = day, startTime = startTime, endTime = endTime)
        events[index] = rescheduledEvent

        // Persist to database and update events
        val changed = withContext(Dispatchers.IO) {
            scheduleRepository.rescheduleEvent(event, day, startTime, endTime)
        }

        // Log result and revert changes on failure
        if (changed) {
            logger.debug { "Successfully rescheduled $rescheduledEvent" }
        } else {
            logger.debug { "Failed to reschedule $event" }
            events[index] = event
        }
    }

    companion object {
        const val TIMETABLE_DEFAULT_START_OF_DAY = 420
        const val TIMETABLE_DEFAULT_END_OF_DAY = 1200
    }
}