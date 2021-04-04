package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

sealed class StudycourseDialogState {
    object Closed : StudycourseDialogState()
    data class Open(val studycourse: Studycourse?) : StudycourseDialogState()
}

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
                        val name = studycourseName.value.text
                        val revision = studycourseRevision.value.text
                        studycourseRepository.insert(name, revision.takeIf(String::isNotBlank))
                        onDismissRequest()
                    }
                }
            )
        }
    ) {
        Column {
            NameInput(studycourseName)

            Spacer(modifier = Modifier.height(8.dp))

            RevisionInput(studycourseRevision)

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun NameInput(name: MutableState<TextFieldValue>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = name.value,
        onValueChange = { value ->
            if (!value.text.contains('\n')) {
                name.value = value
            }
        },
        label = {
            Text(text = l("input_label_studycourse_name"))
        },
        placeholder = {
            Text(text = l("input_hint_studycourse_name"))
        },
    )
}

@Composable
private fun RevisionInput(revision: MutableState<TextFieldValue>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = revision.value,
        onValueChange = { value ->
            if (!value.text.contains('\n')) {
                revision.value = value
            }
        },
        label = {
            Text(text = l("input_label_studycourse_revision"))
        },
        placeholder = {
            Text(text = l("input_hint_studycourse_revision"))
        },
    )
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