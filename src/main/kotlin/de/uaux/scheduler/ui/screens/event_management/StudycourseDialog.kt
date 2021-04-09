package de.uaux.scheduler.ui.screens.event_management

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun StudycourseDialog(studycourse: Studycourse?, onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val studycourseRepository: StudycourseRepository = get()
    val studycourseName = remember { mutableStateOf(TextFieldValue(studycourse?.name.orEmpty())) }
    val studycourseRevision = remember { mutableStateOf(TextFieldValue(studycourse?.revision.orEmpty())) }

    PopupDialog(
        title = l("dialog_title_add_studycourse"),
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_cancel"))
            }

            SaveButton(
                enabled = studycourseName.value.text.isNotBlank(),
                onSave = {
                    coroutineScope.launch {
                        val updated = Studycourse(
                            studycourse?.id ?: -1L,
                            studycourseName.value.text,
                            studycourseRevision.value.text.takeIf(String::isNotBlank),
                        )
                        studycourseRepository.insertOrUpdate(updated)
                        onDismissRequest()
                    }
                }
            )
        }
    ) {
        Column {
            LabeledTextField(
                studycourseName,
                l("input_label_studycourse_name"),
                l("input_hint_studycourse_name"),
            )

            Spacer(modifier = Modifier.height(8.dp))

            LabeledTextField(
                studycourseRevision,
                l("input_label_studycourse_revision"),
                l("input_hint_studycourse_revision"),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SaveButton(
    enabled: Boolean = true,
    onSave: () -> Unit,
) {
    TextButton(
        enabled = enabled,
        onClick = onSave,
    ) {
        Text(text = l("button_text_save"))
    }
}