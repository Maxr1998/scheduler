package de.uaux.scheduler.ui.model

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.Suggestion

@Immutable
sealed class DialogState {
    object Closed : DialogState()
    data class EventDetailsDialog(val studycourseEvent: StudycourseEvent, val scheduledEvent: ScheduledEvent?, val suggestion: Suggestion?) : DialogState()
    data class StudycourseDialog(val studycourse: Studycourse?) : DialogState()
    data class StudycourseEventDialog(val studycourse: Studycourse, val studycourseEvent: StudycourseEvent?) : DialogState()
    data class EventDialog(val event: Event?) : DialogState()
    data class EditSuggestionDialog(val event: Event) : DialogState()
}