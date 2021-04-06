package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.StudycourseEvent
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

sealed class EventManagementDialogState {
    object Closed : EventManagementDialogState()
    data class StudycourseOpened(val studycourse: Studycourse?) : EventManagementDialogState()
}

@Composable
fun EventManagementScreen() = Column {
    Toolbar(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        title = l("screen_event_management"),
    ) {}

    DisableSelection {
        EventManagementScreenContent()
    }
}

@Composable
private fun EventManagementScreenContent() {
    val eventManagementViewModel: EventManagementViewModel = get()
    val studycourseSelection by eventManagementViewModel.studycourseSelection
    val (dialogState, setDialogState) = remember { mutableStateOf<EventManagementDialogState>(EventManagementDialogState.Closed) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row {
            StudycoursesPane(
                studycourseSelection = studycourseSelection,
                openDialog = { studycourse ->
                    setDialogState(EventManagementDialogState.StudycourseOpened(studycourse))
                },
            )
            VerticalDivider()
            EventsPane(studycourseSelection)
        }

        when (dialogState) {
            is EventManagementDialogState.StudycourseOpened -> {
                StudycourseDialog(
                    studycourse = dialogState.studycourse,
                    onDismissRequest = {
                        setDialogState(EventManagementDialogState.Closed)
                    },
                )
            }
            EventManagementDialogState.Closed -> Unit
        }
    }
}

@Composable
private fun StudycoursesPane(
    studycourseSelection: StudycourseSelection,
    openDialog: (Studycourse?) -> Unit,
) {
    val eventManagementViewModel: EventManagementViewModel = get()
    Column(
        modifier = Modifier.width(280.dp).fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            items(eventManagementViewModel.studycourses) { studycourse ->
                val selected = studycourseSelection.isSelected(studycourse)
                StudycourseListItem(
                    modifier = Modifier.selectable(selected) {
                        eventManagementViewModel.load(studycourse)
                    },
                    studycourse = studycourse,
                    selected = selected,
                    openDialog = openDialog,
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            IconButton(
                onClick = { openDialog(null) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StudycourseListItem(
    modifier: Modifier = Modifier,
    studycourse: Studycourse,
    selected: Boolean,
    openDialog: (Studycourse?) -> Unit,
) {
    val background = if (selected) Modifier.background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.12f)) else Modifier
    val textColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

    val secondaryTextContent: @Composable (() -> Unit) = {
        Text(text = studycourse.revision.orEmpty())
    }
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(modifier)
            .then(background),
        text = {
            Text(
                text = studycourse.name,
                color = textColor,
            )
        },
        secondaryText = if (!studycourse.revision.isNullOrBlank()) secondaryTextContent else null,
        trailing = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = {
                        openDialog(studycourse)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@Composable
private fun EventsPane(studycourseSelection: StudycourseSelection) {
    when (studycourseSelection) {
        is StudycourseSelection.None -> {
            CenteredTextMessage(
                text = l("event_panel_no_studycourse_selected"),
            )
        }
        is StudycourseSelection.Selected -> {
            val events by studycourseSelection.events.collectAsState(emptyList())
            if (events.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        EventListHeader()
                    }
                    items(events) { event ->
                        EventListItem(
                            studycourseEvent = event,
                        )
                    }
                }
            } else {
                CenteredTextMessage(
                    text = l("event_panel_no_events"),
                )
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
) {
    val event = studycourseEvent.event
    ListItem(
        modifier = modifier,
        text = {
            Text(text = event.name)
        },
        secondaryText = {
            val semester = l("event_panel_event_summary_semester").format(studycourseEvent.semester)
            val domain = if (studycourseEvent.required) l("studycourse_domain_required") else l("studycourse_domain_voluntary")
            val lecturers = "-" // TODO: list lecturers
            Text(text = "$semester • $domain • $lecturers")
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