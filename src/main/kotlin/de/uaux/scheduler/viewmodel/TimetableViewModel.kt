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
import de.uaux.scheduler.model.dto.UnscheduledEvent
import de.uaux.scheduler.repository.ScheduleRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.repository.SuggestionRepository
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selection
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.model.TimetableFilter
import de.uaux.scheduler.util.binaryInsert
import de.uaux.scheduler.util.binaryInsertIndex
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
    val timetableSelection: State<Selection<TimetableFilter>> get() = _timetableSelection
    private val _timetableSelection: MutableState<Selection<TimetableFilter>> = mutableStateOf(None)

    /**
     * Contains [ScheduledEvent]s for the currently selected schedule
     */
    val events: SnapshotStateList<ScheduledEvent> = mutableStateListOf()

    /**
     * Contains [Event]s for the studycourse and semester that are currently not scheduled
     */
    val unscheduledEvents: SnapshotStateList<UnscheduledEvent> = mutableStateListOf()

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
        _timetableSelection.value = Loading

        val eventsJob = launch {
            // Refresh scheduled events
            val scheduled = withContext(Dispatchers.IO) {
                scheduleRepository.queryScheduledEvents(studycourse, semester)
            }

            val hasWeekends = scheduled.any { event -> event.day > DayOfWeek.FRIDAY }
            showWeekend.value = if (hasWeekends) ShowWeekend.FORCE else ShowWeekend.FALSE

            events.clear()
            events.addAll(scheduled)
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

        val unscheduledJob = launch {
            // Refresh unscheduled events
            val unscheduled = withContext(Dispatchers.IO) {
                scheduleRepository.queryUnscheduledEvents(studycourse, semester).filter { unscheduledEvent ->
                    unscheduledEvent.count > 0 // TODO: should be filtered in database
                }
            }

            unscheduledEvents.clear()
            unscheduledEvents.addAll(unscheduled)
        }

        joinAll(eventsJob, timeslotsJob, unscheduledJob)

        _timetableSelection.value = Selection(TimetableFilter(semester, studycourse))
    }

    fun schedule(event: ScheduledEvent) = coroutineScope.launch {
        logger.debug { "Scheduling $event" }

        // Assert start and end times
        require(event.startTime in 0..MAX_MINUTES_IN_DAY)
        require(event.endTime in 0..MAX_MINUTES_IN_DAY)

        // Apply changes to ViewModel
        val insertIndex = events.binaryInsertIndex(event)
        events.add(insertIndex, event)

        // Persist to database and update events
        val changed = withContext(Dispatchers.IO) {
            scheduleRepository.scheduleEvent(event)
        }

        // Log result and revert changes on failure
        if (changed) {
            logger.debug { "Successfully added $event to schedule" }
            val removeOrReplaceIndex = unscheduledEvents.binarySearch { ue ->
                unscheduledEventsComparator.compare(ue.event, event.event)
            }
            require(removeOrReplaceIndex >= 0)
            val unscheduledEvent = unscheduledEvents[removeOrReplaceIndex]
            when (unscheduledEvent.count) {
                1 -> unscheduledEvents.removeAt(removeOrReplaceIndex)
                else -> unscheduledEvents[removeOrReplaceIndex] = unscheduledEvent.copy(count = unscheduledEvent.count - 1)
            }
        } else {
            logger.debug { "Failed to add $event to schedule" }
            events.removeAt(insertIndex)
        }
    }

    fun reschedule(event: ScheduledEvent, day: DayOfWeek, startTime: Int) = coroutineScope.launch {
        logger.debug { "Rescheduling $event to $day at $startTime" }

        // Assert start and end times
        require(startTime in 0..MAX_MINUTES_IN_DAY)
        require(startTime + event.duration in 0..MAX_MINUTES_IN_DAY)

        // Get event index and check if present
        val index = events.binarySearch(event)
        if (index < 0) {
            // Abort if invalid event
            logger.error { "$event not in list" }
            return@launch
        }

        // Apply changes to ViewModel
        events.removeAt(index)
        val rescheduledEvent = event.copy(day = day, startTime = startTime)
        val insertIndex = events.binaryInsertIndex(rescheduledEvent)
        events.add(insertIndex, rescheduledEvent)

        // Persist to database and update events
        val changed = withContext(Dispatchers.IO) {
            scheduleRepository.rescheduleEvent(event, day, startTime)
        }

        // Log result and revert changes on failure
        if (changed) {
            logger.debug { "Successfully rescheduled $rescheduledEvent" }
        } else {
            logger.debug { "Failed to reschedule $event" }

            // Rollback changes
            events.removeAt(insertIndex)
            events.binaryInsert(event)
        }
    }

    fun unschedule(event: ScheduledEvent) = coroutineScope.launch {
        logger.debug { "Removing $event from schedule" }

        // Get event index and check if present
        val index = events.binarySearch(event)
        if (index < 0) {
            // Abort if invalid event
            logger.error { "$event not in list" }
            return@launch
        }

        // Apply changes to ViewModel
        events.removeAt(index)

        // Persist to database and update events
        val changed = withContext(Dispatchers.IO) {
            scheduleRepository.unscheduleEvent(event)
        }

        // Log result and revert changes on failure
        if (changed) {
            logger.debug { "Successfully unscheduled $event" }
            val unscheduledEvent = UnscheduledEvent(event.semester, event.studycourseEvent, 1)
            unscheduledEvents.binaryInsert(unscheduledEvent) { ue ->
                unscheduledEventsComparator.compare(ue.event, event.event)
            }
        } else {
            logger.debug { "Failed to unschedule $event" }

            // Rollback changes
            events.binaryInsert(event)
        }
    }

    suspend fun getSuggestion(semester: Semester, event: Event): Suggestion? = withContext(Dispatchers.IO) {
        suggestionRepository.querySuggestionBySemesterAndEvent(semester, event)
    }

    companion object {
        const val MAX_MINUTES_IN_DAY = 1439
        const val TIMETABLE_DEFAULT_START_OF_DAY = 420
        const val TIMETABLE_DEFAULT_END_OF_DAY = 1200

        val unscheduledEventsComparator: Comparator<Event> = compareBy(Event::name, Event::id)
    }
}