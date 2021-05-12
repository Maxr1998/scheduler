package de.uaux.scheduler.ui.screens.management.event

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l

@Composable
fun LecturersDialog(onDismissRequest: () -> Unit) {
    PopupDialog(
        title = l("dialog_title_lecturers"),
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_close"))
            }
        },
    ) {

    }
}