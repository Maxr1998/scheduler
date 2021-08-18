package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Semester

@Immutable
data class UnscheduledEvent(
    val semester: Semester,
    val studycourseEvent: StudycourseEvent,
    val count: Int,
) {
    val event = studycourseEvent.event
}