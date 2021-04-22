package de.uaux.scheduler.ui.model

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.Studycourse

@Immutable
data class TimetableFilter(val semester: Semester, val studycourse: Studycourse)