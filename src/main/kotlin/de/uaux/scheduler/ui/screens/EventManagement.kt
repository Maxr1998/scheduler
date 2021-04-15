package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.DialogState
import de.uaux.scheduler.ui.screens.event_management.EventsPane
import de.uaux.scheduler.ui.screens.event_management.StudycoursesPane
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.DialogViewModel
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
    val dialogViewModel: DialogViewModel = get()
    val eventManagementViewModel: EventManagementViewModel = get()
    val studycourseSelection by eventManagementViewModel.studycourseSelection

    Row {
        StudycoursesPane(
            studycourseSelection = studycourseSelection,
            openDialog = { studycourse ->
                dialogViewModel.openDialog(DialogState.StudycourseDialog(studycourse))
            },
        )
        VerticalDivider()
        EventsPane(
            studycourseSelection = studycourseSelection,
            openEventDialog = { event ->
                dialogViewModel.openDialog(DialogState.EventDialog(event))
            },
            openStudycourseEventDialog = { studycourse, studycourseEvent ->
                dialogViewModel.openDialog(DialogState.StudycourseEventDialog(studycourse, studycourseEvent))
            },
        )
    }
}