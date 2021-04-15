package de.uaux.scheduler.ui

import androidx.compose.runtime.Composable
import de.uaux.scheduler.ui.model.DialogState
import de.uaux.scheduler.ui.screens.management.event.EventDialog
import de.uaux.scheduler.ui.screens.management.studycourse.StudycourseDialog
import de.uaux.scheduler.ui.screens.management.studycourse.StudycourseEventDialog
import de.uaux.scheduler.viewmodel.DialogViewModel
import org.koin.androidx.compose.get

@Composable
fun Dialogs() {
    val dialogViewModel: DialogViewModel = get()
    val (dialogState, setDialogState) = dialogViewModel.dialogState

    when (dialogState) {
        is DialogState.StudycourseDialog -> {
            StudycourseDialog(
                studycourse = dialogState.studycourse,
                onDismissRequest = {
                    setDialogState(DialogState.Closed)
                },
            )
        }
        is DialogState.StudycourseEventDialog -> {
            StudycourseEventDialog(
                studycourse = dialogState.studycourse,
                studycourseEvent = dialogState.studycourseEvent,
                onCreateEventRequest = {
                    setDialogState(DialogState.EventDialog(null))
                },
                onDismissRequest = {
                    setDialogState(DialogState.Closed)
                },
            )
        }
        is DialogState.EventDialog -> {
            EventDialog(
                event = dialogState.event,
                onDismissRequest = {
                    setDialogState(DialogState.Closed)
                }
            )
        }
        DialogState.Closed -> Unit
    }
}