package de.uaux.scheduler.ui.model

import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import kotlinx.coroutines.flow.Flow

sealed class StudycourseSelection {
    class None(val allEvents: Flow<List<Event>>) : StudycourseSelection()
    class Selected(val studycourse: Studycourse, val events: Flow<List<StudycourseEvent>>) : StudycourseSelection()

    fun isSelected(studycourse: Studycourse) = this is Selected && this.studycourse == studycourse
}