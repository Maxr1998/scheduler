package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Room
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.util.formatMinutesOfDay
import java.time.DayOfWeek

@Immutable
data class ScheduledEvent(
    val semester: Semester,
    val event: Event,
    val day: DayOfWeek,
    val startTime: Int,
    val room: Room?,
) {
    val duration: Int = event.duration
    val endTime: Int = startTime + event.duration

    override fun toString(): String =
        "ScheduledEvent(${event.name}, $semester, $day / ${formatMinutesOfDay(startTime)} - ${formatMinutesOfDay(endTime)}, room=${room?.id})"

    fun toShortString(): String = "ScheduledEvent(${event.name})"
}