package de.uaux.scheduler.ui.screens.event_management

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l

@Composable
fun StudycourseEventDialog(studycourse: Studycourse, studycourseEvent: StudycourseEvent?, onDismissRequest: () -> Unit) {
    PopupDialog(
        title = l(if (studycourseEvent == null) "dialog_title_add_studycourse_event" else "dialog_title_edit_studycourse_event"),
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_cancel"))
            }
        }
    ) {}
}