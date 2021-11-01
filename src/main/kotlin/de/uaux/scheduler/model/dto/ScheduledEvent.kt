package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Room
import de.uaux.scheduler.model.Schedule
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.util.formatTimeMinutesOfDay
import java.time.DayOfWeek

@Immutable
data class ScheduledEvent(
    val semester: Semester,
    val studycourseEvent: StudycourseEvent,
    val day: DayOfWeek,
    val startTime: Int,
    val room: Room?,
) : Comparable<ScheduledEvent> {
    val event = studycourseEvent.event
    val duration: Int = event.duration
    val endTime: Int = startTime + event.duration

    override fun toString(): String =
        "ScheduledEvent(${event.name}, $semester, $day / ${formatTimeMinutesOfDay(startTime)} - ${formatTimeMinutesOfDay(endTime)}, room=${room?.id})"

    fun toShortString(): String = "ScheduledEvent(${event.name})"

    fun persist(): Schedule = Schedule(semester.code, event.id, day.value, startTime, room?.id ?: 0L)

    override fun compareTo(other: ScheduledEvent): Int {
        if (this == other) return 0
        val byDay = day.compareTo(other.day)
        if (byDay != 0) return byDay

        val byTime = startTime.compareTo(other.startTime)
        if (byTime != 0) return byTime

        val byDuration = -duration.compareTo(other.duration)
        if (byDuration != 0) return byDuration

        return -1 // Default less
    }
}