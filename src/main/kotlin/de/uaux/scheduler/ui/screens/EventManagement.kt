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
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.selection.DisableSelection
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

    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            StudycoursesPane(studycourseSelection)
            VerticalDivider()
            EventsPane(studycourseSelection)
        }
    }
}

@Composable
private fun StudycoursesPane(studycourseSelection: StudycourseSelection) {
    val eventManagementViewModel: EventManagementViewModel = get()
    Column(
        modifier = Modifier.preferredWidth(280.dp).fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            eventManagementViewModel.studycourses.let { studycourses ->
                val count = studycourses.size
                items(count) { i ->
                    val studycourse = studycourses[i]
                    val selected = studycourseSelection.isSelected(studycourse)
                    StudycourseListItem(
                        modifier = Modifier.selectable(selected) {
                            eventManagementViewModel.load(studycourse)
                        },
                        studycourse = studycourse,
                        selected = selected,
                    )
                }
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            IconButton(
                onClick = {
                    // TODO: open dialog to add a studycourse
                },
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

@Composable
private fun StudycourseListItem(
    modifier: Modifier = Modifier,
    studycourse: Studycourse,
    selected: Boolean,
) {
    val background = if (selected) Modifier.background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.12f)) else Modifier
    val textColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(modifier)
            .then(background)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = studycourse.name,
            color = textColor,
            style = MaterialTheme.typography.body2,
        )
    }
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
            val items = studycourseSelection.events.collectAsState(emptyList()).value
            if (items.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        EventListHeader()
                    }
                    items(items.size) {
                        EventListItem(
                            studycourseEvent = items[it],
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