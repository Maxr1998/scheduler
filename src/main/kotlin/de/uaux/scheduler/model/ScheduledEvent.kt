package de.uaux.scheduler.model

import androidx.compose.runtime.Immutable
import java.time.DayOfWeek

@Immutable
data class ScheduledEvent(
    val semester: Semester,
    val event: Event,
    val day: DayOfWeek,
    val startTime: Int,
    val endTime: Int,
    val room: Room?,
)