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
import de.uaux.scheduler.util.LocalizationUtil
import de.uaux.scheduler.util.ScheduledEventMapper
import de.uaux.scheduler.util.StudycourseEventMapper
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
import java.time.Month.JUNE
import java.time.Month.MAY
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
                    query.executeAsMappedList { semester ->
                        Semester(semester)
                    }.ifEmpty {
                        listOf(computeNextSemester())
                    }
                }
            }

    fun computeNextSemester(): Semester {
        val now = LocalDate.now()
        return when (now.month) {
            in JANUARY..MAY -> Semester(SS, now.year)
            in JUNE..NOVEMBER -> Semester(WS, now.year)
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
        val roomCache = HashMap<Long, Room>()
        val mapper: ScheduledEventMapper = { id, name, module, duration, participants, studycourseSemester, required, day, startTime, roomId ->
            val room = queryRoom(roomId, roomCache)?.also { room ->
                roomCache.putIfAbsent(roomId, room)
            }
            val event = Event(id, name, module, duration, participants)
            val studycourseEvent = StudycourseEvent(event, studycourseSemester, required)
            ScheduledEvent(semester, studycourseEvent, DayOfWeek.of(day), startTime, room)
        }
        return scheduleQueries.queryScheduledEventsInStudycourseBySemester(studycourse.id, semester.code, mapper = mapper).executeAsList()
    }

    fun queryUnscheduledEvents(studycourse: Studycourse, semester: Semester): List<StudycourseEvent> {
        val mapper: StudycourseEventMapper = { id, name, module, duration, participants, studycourseSemester, required ->
            val event = Event(id, name, module, duration, participants)
            StudycourseEvent(event, studycourseSemester, required)
        }
        return scheduleQueries.queryUnscheduledEventsInStudycourseBySemester(studycourse.id, semester.code, mapper = mapper).executeAsList()
    }

    fun queryRoom(id: Long, cache: Map<Long, Room> = emptyMap()): Room? = when (id) {
        in cache -> cache[id]
        0L -> null
        -1L -> Room(-1, localizationUtil["room_digital"], Int.MAX_VALUE)
        else -> roomQueries.queryRoomById(id).executeAsOneOrNull()
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
}