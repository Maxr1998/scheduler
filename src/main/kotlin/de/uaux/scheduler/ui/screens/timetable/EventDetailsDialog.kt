package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.ui.screens.management.event.suggestion.SuggestionDetails
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.util.formatMinutesOfDay
import java.time.format.TextStyle
import java.util.*

@Composable
fun EventDetailsDialog(
    studycourseEvent: StudycourseEvent,
    scheduledEvent: ScheduledEvent?,
    suggestion: Suggestion?,
    onDismissRequest: () -> Unit,
) {
    PopupDialog(
        title = studycourseEvent.event.name,
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_close"))
            }
        },
    ) {
        Column {
            Text(
                text = l("section_label_event_details"),
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle1,
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (scheduledEvent != null) {
                val day = remember(scheduledEvent.day) { scheduledEvent.day.getDisplayName(TextStyle.FULL, Locale.getDefault()) }

                Text(
                    text = "$day, ${formatMinutesOfDay(scheduledEvent.startTime)}-${formatMinutesOfDay(scheduledEvent.endTime)}",
                    style = MaterialTheme.typography.body1,
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = l("event_label_recommended_semester").format(studycourseEvent.semester),
                style = MaterialTheme.typography.body1,
            )

            if (suggestion != null && (suggestion.text.isNotBlank() || suggestion.constraints.isNotEmpty())) {
                Spacer(modifier = Modifier.height(16.dp))

                SuggestionDetails(
                    initialText = suggestion.text,
                    initialConstraints = suggestion.constraints,
                )
            }
        }
    }
}