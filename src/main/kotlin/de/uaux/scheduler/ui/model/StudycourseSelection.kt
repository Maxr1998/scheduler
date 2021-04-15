package de.uaux.scheduler.ui.model

import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import kotlinx.coroutines.flow.Flow

sealed class StudycourseSelection {
    object None : StudycourseSelection()
    class Selected(val studycourse: Studycourse, val events: Flow<List<StudycourseEvent>>) : StudycourseSelection()

    fun isSelected(studycourse: Studycourse) = this is Selected && this.studycourse == studycourse
}