package de.uaux.scheduler.ui.screens.event_management

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.l

@Composable
fun EventsPane(
    studycourseSelection: StudycourseSelection,
    openEventDialog: (Event?) -> Unit,
    openStudycourseEventDialog: (StudycourseEvent?) -> Unit,
) {
    when (studycourseSelection) {
        is StudycourseSelection.None -> {
            val events by studycourseSelection.allEvents.collectAsState(emptyList())
            EventListContent(
                events = events,
                onAdd = { openEventDialog(null) },
            ) { event ->
                EventListItem(
                    event = event,
                    openDialog = openEventDialog,
                )
            }
        }
        is StudycourseSelection.Selected -> {
            val events by studycourseSelection.events.collectAsState(emptyList())
            EventListContent(
                events = events,
                onAdd = { openStudycourseEventDialog(null) },
            ) { event ->
                StudycourseEventListItem(
                    studycourseEvent = event,
                    openDialog = openStudycourseEventDialog,
                )
            }
        }
    }
}

@Composable
private fun <T> EventListContent(events: List<T>, onAdd: () -> Unit, eventContent: @Composable (T) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp),
                        text = l("event_panel_header"),
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.caption,
                    )
                }
                items(events) { event ->
                    eventContent(event)
                }
            }
        } else {
            CenteredTextMessage(
                text = l("event_panel_no_events"),
            )
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
            onClick = { onAdd() },
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventListItem(
    modifier: Modifier = Modifier,
    event: Event,
    openDialog: (Event?) -> Unit,
) {
    ListItem(
        modifier = modifier,
        text = {
            Text(text = event.name)
        },
        trailing = {
            EditButton(
                onClick = { openDialog(event) },
            )
        },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StudycourseEventListItem(
    modifier: Modifier = Modifier,
    studycourseEvent: StudycourseEvent,
    openDialog: (StudycourseEvent?) -> Unit,
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
                onClick = { openDialog(studycourseEvent) },
            )
        },
    )
}

@Composable
private fun CenteredTextMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
        )
    }
}