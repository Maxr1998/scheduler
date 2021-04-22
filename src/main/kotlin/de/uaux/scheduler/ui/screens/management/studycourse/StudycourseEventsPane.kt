package de.uaux.scheduler.ui.screens.management.studycourse

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.screens.management.EventListContent
import de.uaux.scheduler.ui.util.CenteredTextBox
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.LoadingBox
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selected
import de.uaux.scheduler.ui.model.Selection
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

@Composable
fun StudycourseEventsPane(
    selection: Selection<Studycourse>,
    openStudycourseEventDialog: (Studycourse, StudycourseEvent?) -> Unit,
) {
    when (selection) {
        None -> CenteredTextBox(text = l("event_panel_no_studycourses"))
        Loading -> LoadingBox()
        is Selected -> {
            val eventManagementViewModel: EventManagementViewModel = get()
            val eventsFlow = remember(selection.value) { eventManagementViewModel.getStudycourseEvents(selection.value) }
            val events by eventsFlow.collectAsState(emptyList())
            EventListContent(
                events = events,
                fabIcon = Icons.Outlined.Link,
                onAdd = { openStudycourseEventDialog(selection.value, null) },
            ) { event ->
                StudycourseEventListItem(
                    studycourseEvent = event,
                    openDialog = {
                        openStudycourseEventDialog(selection.value, event)
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
                l("event_panel_event_summary_semester").format(studycourseEvent.semester),
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