package de.uaux.scheduler.ui

import androidx.compose.runtime.Composable
import de.uaux.scheduler.ui.model.DialogState
import de.uaux.scheduler.ui.screens.management.event.EventDialog
import de.uaux.scheduler.ui.screens.management.event.suggestion.EditSuggestionDialog
import de.uaux.scheduler.ui.screens.management.event.suggestion.SuggestionDialog
import de.uaux.scheduler.ui.screens.management.studycourse.StudycourseDialog
import de.uaux.scheduler.ui.screens.management.studycourse.StudycourseEventDialog
import de.uaux.scheduler.viewmodel.DialogViewModel
import org.koin.androidx.compose.get

@Composable
fun Dialogs() {
    val dialogViewModel: DialogViewModel = get()
    val (dialogState, setDialogState) = dialogViewModel.dialogState
    val closeDialog = { setDialogState(DialogState.Closed) }

    when (dialogState) {
        is DialogState.StudycourseDialog -> {
            StudycourseDialog(
                studycourse = dialogState.studycourse,
                onDismissRequest = closeDialog,
            )
        }
        is DialogState.StudycourseEventDialog -> {
            StudycourseEventDialog(
                studycourse = dialogState.studycourse,
                studycourseEvent = dialogState.studycourseEvent,
                onCreateEventRequest = {
                    setDialogState(DialogState.EventDialog(null))
                },
                onDismissRequest = closeDialog,
            )
        }
        is DialogState.EventDialog -> {
            EventDialog(
                event = dialogState.event,
                onDismissRequest = closeDialog,
            )
        }
        is DialogState.SuggestionDialog -> {
            SuggestionDialog(
                suggestion = dialogState.suggestion,
                onDismissRequest = closeDialog,
            )
        }
        is DialogState.EditSuggestionDialog -> {
            EditSuggestionDialog(
                event = dialogState.event,
                onDismissRequest = closeDialog,
            )
        }
        DialogState.Closed -> Unit
    }
}