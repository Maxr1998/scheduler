package de.uaux.scheduler.ui.model

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse

@Immutable
sealed class TimetableSelection {
    object None : TimetableSelection()
    object Loading : TimetableSelection()
    class Loaded(val semester: Semester, val studycourse: Studycourse) : TimetableSelection()
}