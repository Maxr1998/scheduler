package de.uaux.scheduler.ui.screens.event_management

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun StudycourseEventDialog(studycourse: Studycourse, studycourseEvent: StudycourseEvent?, onDismissRequest: () -> Unit) {
    val studycourseRepository: StudycourseRepository = get()

    PopupDialog(
        title = l(if (studycourseEvent == null) "dialog_title_add_studycourse_event" else "dialog_title_edit_studycourse_event"),
        onDismissRequest = onDismissRequest,
        actions = {
            val coroutineScope = rememberCoroutineScope()

            if (studycourseEvent != null) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            studycourseRepository.unlink(studycourse, studycourseEvent.event)
                            onDismissRequest()
                        }
                    },
                ) {
                    Text(text = l("button_remove_studycourse_event"))
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_cancel"))
            }
        }
    ) {}
}