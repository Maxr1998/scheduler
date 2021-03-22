package de.uaux.scheduler.model

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.util.formatMinutesOfDay
import java.time.DayOfWeek

@Immutable
data class ScheduledEvent(
    val semester: Semester,
    val event: Event,
    val day: DayOfWeek,
    val startTime: Int,
    val endTime: Int,
    val room: Room?,
) {
    val duration: Int = endTime - startTime

    override fun toString(): String =
        "ScheduledEvent(${event.name}, $semester, $day / ${formatMinutesOfDay(startTime)} - ${formatMinutesOfDay(endTime)}, room=${room?.id})"
}