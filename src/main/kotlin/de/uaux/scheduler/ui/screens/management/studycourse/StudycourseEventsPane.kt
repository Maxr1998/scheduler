package de.uaux.scheduler.ui.screens.management.studycourse

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.ui.screens.management.EventListContent
import de.uaux.scheduler.ui.util.CenteredTextBox
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.l

@Composable
fun StudycourseEventsPane(
    studycourseSelection: StudycourseSelection,
    openStudycourseEventDialog: (Studycourse, StudycourseEvent?) -> Unit,
) {
    when (studycourseSelection) {
        is StudycourseSelection.None -> {
            CenteredTextBox(text = l("event_panel_no_studycourses"))
        }
        is StudycourseSelection.Selected -> {
            val events by studycourseSelection.events.collectAsState(emptyList())
            EventListContent(
                events = events,
                fabIcon = Icons.Outlined.Link,
                onAdd = { openStudycourseEventDialog(studycourseSelection.studycourse, null) },
            ) { event ->
                StudycourseEventListItem(
                    studycourseEvent = event,
                    openDialog = {
                        openStudycourseEventDialog(studycourseSelection.studycourse, event)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StudycourseEventListItem(
    modifier: Modifier = Modifier,
    studycourseEvent: StudycourseEvent,
    openDialog: () -> Unit,
) {
    val event = studycourseEvent.event
    ListItem(
        modifier = modifier,
        text = {
            Text(text = event.name)
        },
        secondaryText = {
            val fields = listOfNotNull(
                studycourseEvent.semester?.let { semester -> l("event_panel_event_summary_semester").format(semester) },
                if (studycourseEvent.required) l("studycourse_domain_required") else l("studycourse_domain_voluntary"),
                "-", // TODO: list lecturers
            )
            Text(text = fields.joinToString(" • "))
        },
        trailing = {
            EditButton(
                onClick = openDialog,
            )
        },
    )
}