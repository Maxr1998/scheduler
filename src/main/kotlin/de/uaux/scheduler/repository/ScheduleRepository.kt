package de.uaux.scheduler.repository

import com.squareup.sqldelight.runtime.coroutines.asFlow
import de.uaux.scheduler.model.Database
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Room
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Semester.Type.SS
import de.uaux.scheduler.model.Semester.Type.WS
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.UnscheduledEvent
import de.uaux.scheduler.util.LocalizationUtil
import de.uaux.scheduler.util.ScheduledEventMapper
import de.uaux.scheduler.util.UnscheduledEventMapper
import de.uaux.scheduler.util.executeAsMappedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.sql.SQLException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month.DECEMBER
import java.time.Month.JANUARY
import java.time.Month.JULY
import java.time.Month.JUNE
import java.time.Month.NOVEMBER

class ScheduleRepository(
    database: Database,
    private val localizationUtil: LocalizationUtil,
) {
    private val standardQueries = database.standardQueries
    private val scheduleQueries = database.scheduleQueries
    private val timeslotQueries = database.timeslotQueries
    private val roomQueries = database.roomQueries

    val allSemestersFlow: Flow<List<Semester>> =
        scheduleQueries
            .queryAllSemesters()
            .asFlow()
            .map { query ->
                withContext(Dispatchers.IO) {
                    val semesters = query.executeAsMappedList { semester -> Semester(semester) }

                    // Add "next" semester if not in database already
                    val nextSemester = computeNextSemester()
                    val insertIndex = semesters.binarySearch { s ->
                        nextSemester.code - s.code // Inverted comparison as the list is sorted in descending order
                    }
                    when {
                        insertIndex < 0 -> semesters.toMutableList().apply {
                            add(-(insertIndex + 1), nextSemester)
                        }
                        else -> semesters
                    }
                }
            }

    fun computeNextSemester(): Semester {
        val now = LocalDate.now()
        return when (now.month) {
            in JANUARY..JUNE -> Semester(SS, now.year)
            in JULY..NOVEMBER -> Semester(WS, now.year)
            DECEMBER -> Semester(SS, now.year + 1)
            else -> throw IllegalStateException("Month outside of possible range")
        }
    }

    fun queryTimeslots(semester: Semester): List<Timeslot> =
        timeslotQueries.queryTimeslotsForSemester(semester.code).executeAsList()

    /**
     * List of scheduled events for [studycourse] in [semester],
     * pre-sorted by [day][ScheduledEvent.day] and [start time][ScheduledEvent.startTime]
     */
    fun queryScheduledEvents(studycourse: Studycourse, semester: Semester): List<ScheduledEvent> {
        return scheduleQueries.queryScheduledEventsInStudycourseBySemester(
            studycourse = studycourse.id,
            semester = semester.code,
            mapper = getScheduledEventMapper(semester),
        ).executeAsList()
    }

    fun queryUnscheduledEvents(studycourse: Studycourse, semester: Semester): List<UnscheduledEvent> {
        val mapper: UnscheduledEventMapper = { id, name, type, module, duration, participants, studycourseSemester, required, count ->
            val event = Event(id, name, type, module, duration, participants)
            val studycourseEvent = StudycourseEvent(event, studycourseSemester, required)
            UnscheduledEvent(semester, studycourseEvent, count.toInt())
        }
        return scheduleQueries.queryUnscheduledEventsInStudycourseBySemester(studycourse.id, semester.code, mapper = mapper).executeAsList()
    }

    fun queryConflictsWithEvent(studycourse: Studycourse, scheduledEvent: ScheduledEvent): List<ScheduledEvent> {
        val semester = scheduledEvent.semester
        return scheduleQueries.queryConflictsWithEvent(
            studycourse = studycourse.id,
            event = scheduledEvent.event.id,
            semester = semester.code,
            day = scheduledEvent.day.value,
            start = scheduledEvent.startTime,
            end = scheduledEvent.startTime + scheduledEvent.duration,
            mapper = getScheduledEventMapper(semester),
        ).executeAsList()
    }

    fun queryRoom(id: Long, cache: Map<Long, Room> = emptyMap()): Room? = when (id) {
        in cache -> cache[id]
        0L -> null
        -1L -> Room(-1, localizationUtil["room_digital"], Int.MAX_VALUE)
        else -> roomQueries.queryRoomById(id).executeAsOneOrNull()
    }

    fun getEventCount(event: Event, semester: Semester): Int {
        return scheduleQueries.getEventCount(semester = semester.code, event = event.id).executeAsOneOrNull() ?: 1
    }

    fun setEventCount(event: Event, semester: Semester, count: Int) {
        require(count >= 1)
        when (count) {
            1 -> scheduleQueries.unsetEventCount(semester = semester.code, event = event.id)
            else -> scheduleQueries.setEventCount(semester = semester.code, event = event.id, count = count)
        }
    }

    fun scheduleEvent(event: ScheduledEvent): Boolean {
        scheduleQueries.insert(event.persist())
        return standardQueries.changes().executeAsOne() == 1L
    }

    fun rescheduleEvent(event: ScheduledEvent, day: DayOfWeek, startTime: Int): Boolean = try {
        scheduleQueries.rescheduleEvent(
            day.value, startTime,
            event.semester.code, event.event.id, event.day.value, event.startTime,
        )
        standardQueries.changes().executeAsOne() == 1L
    } catch (e: SQLException) {
        false
    }

    fun unscheduleEvent(event: ScheduledEvent): Boolean = try {
        scheduleQueries.unscheduleEvent(event.semester.code, event.event.id, event.day.value, event.startTime)
        standardQueries.changes().executeAsOne() == 1L
    } catch (e: SQLException) {
        false
    }

    private fun getScheduledEventMapper(semester: Semester): ScheduledEventMapper {
        val roomCache = HashMap<Long, Room>()
        return { id, name, type, module, duration, participants, studycourseSemester, required, day, startTime, roomId ->
            val room = queryRoom(roomId, roomCache)?.also { room ->
                roomCache.putIfAbsent(roomId, room)
            }
            val event = Event(id, name, type, module, duration, participants)
            val studycourseEvent = StudycourseEvent(event, studycourseSemester, required)
            ScheduledEvent(semester, studycourseEvent, DayOfWeek.of(day), startTime, room)
        }
    }
}