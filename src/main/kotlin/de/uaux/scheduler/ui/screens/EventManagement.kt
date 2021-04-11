package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.ui.screens.event_management.EventDialog
import de.uaux.scheduler.ui.screens.event_management.EventsPane
import de.uaux.scheduler.ui.screens.event_management.StudycourseDialog
import de.uaux.scheduler.ui.screens.event_management.StudycourseEventDialog
import de.uaux.scheduler.ui.screens.event_management.StudycoursesPane
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

sealed class EventManagementDialogState {
    object Closed : EventManagementDialogState()
    data class StudycourseOpened(val studycourse: Studycourse?) : EventManagementDialogState()
    data class StudycourseEventOpened(val studycourseEvent: StudycourseEvent?) : EventManagementDialogState()
    data class EventOpened(val event: Event?) : EventManagementDialogState()
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
            EventsPane(
                studycourseSelection = studycourseSelection,
                openEventDialog = { event ->
                    setDialogState(EventManagementDialogState.EventOpened(event))
                },
                openStudycourseEventDialog = { studycourseEvent ->
                    setDialogState(EventManagementDialogState.StudycourseEventOpened(studycourseEvent))
                },
            )
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
            is EventManagementDialogState.StudycourseEventOpened -> {
                val studycourse = (studycourseSelection as StudycourseSelection.Selected).studycourse
                StudycourseEventDialog(
                    studycourse = studycourse,
                    studycourseEvent = dialogState.studycourseEvent,
                    onCreateEventRequest = {
                        setDialogState(EventManagementDialogState.EventOpened(null))
                    },
                    onDismissRequest = {
                        setDialogState(EventManagementDialogState.Closed)
                    },
                )
            }
            is EventManagementDialogState.EventOpened -> {
                EventDialog(
                    event = dialogState.event,
                    onDismissRequest = {
                        setDialogState(EventManagementDialogState.Closed)
                    }
                )
            }
            EventManagementDialogState.Closed -> Unit
        }
    }
}