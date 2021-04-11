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
import de.uaux.scheduler.util.LocalizationUtil
import de.uaux.scheduler.util.executeAsMappedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
    private val eventQueries = database.eventQueries
    private val timeslotQueries = database.timeslotQueries
    private val scheduleQueries = database.scheduleQueries
    private val roomQueries = database.roomQueries

    val allSemestersFlow: Flow<List<Semester>> =
        scheduleQueries
            .queryAllSemesters()
            .asFlow()
            .map { query ->
                withContext(Dispatchers.IO) {
                    query.executeAsMappedList { semester -> Semester(semester) }
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

    fun queryScheduledEvents(studycourse: Studycourse, semester: Semester): List<ScheduledEvent> {
        val roomCache = HashMap<Long, Room>()
        return eventQueries.queryScheduledEvents(studycourse.id, semester.code) { id, name, module, participants, day, startTime, endTime, roomId ->
            val room = queryRoom(roomId, roomCache)?.also { room ->
                roomCache.putIfAbsent(roomId, room)
            }
            ScheduledEvent(semester, Event(id, name, module, participants), DayOfWeek.of(day), startTime, endTime, room)
        }.executeAsList()
    }

    fun queryRoom(id: Long, cache: Map<Long, Room> = emptyMap()): Room? = when (id) {
        in cache -> cache[id]
        -1L -> Room(-1, localizationUtil["room_digital"], Int.MAX_VALUE)
        else -> roomQueries.queryRoomById(id).executeAsOneOrNull()
    }

    fun rescheduleEvent(event: ScheduledEvent, day: DayOfWeek, startTime: Int, endTime: Int): Boolean {
        scheduleQueries.rescheduleEvent(
            day.value, startTime, endTime,
            event.semester.code, event.event.id, event.day.value, event.startTime, event.endTime
        )
        return standardQueries.changes().executeAsOne() == 1L
    }
}