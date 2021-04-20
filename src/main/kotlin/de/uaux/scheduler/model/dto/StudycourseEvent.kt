package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.StudycourseEvent as DbStudycourseEvent

@Immutable
data class StudycourseEvent(
    val event: Event,
    val semester: Int,
    val required: Boolean,
) {
    fun persist(studycourse: Studycourse): DbStudycourseEvent = DbStudycourseEvent(studycourse.id, event.id, semester, required)
}