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
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.l

@Composable
fun EventsPane(
    studycourseSelection: StudycourseSelection,
    openDialog: (StudycourseEvent?) -> Unit,
) {
    when (studycourseSelection) {
        is StudycourseSelection.None -> {
            CenteredTextMessage(
                text = l("event_panel_no_studycourse_selected"),
            )
        }
        is StudycourseSelection.Selected -> {
            Box(modifier = Modifier.fillMaxSize()) {
                val events by studycourseSelection.events.collectAsState(emptyList())
                if (events.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        item {
                            EventListHeader()
                        }
                        items(events) { event ->
                            EventListItem(
                                studycourseEvent = event,
                                openDialog = openDialog,
                            )
                        }
                    }
                } else {
                    CenteredTextMessage(
                        text = l("event_panel_no_events"),
                    )
                }

                FloatingActionButton(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
                    onClick = {
                        openDialog(null)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
private fun EventListHeader() {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = l("event_panel_header"),
        color = MaterialTheme.colors.secondary,
        style = MaterialTheme.typography.caption,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventListItem(
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