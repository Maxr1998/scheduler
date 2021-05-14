package de.uaux.scheduler.ui.screens.management.event

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import de.uaux.scheduler.repository.LecturerRepository
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import org.koin.androidx.compose.get

@Composable
fun LecturersDialog(onDismissRequest: () -> Unit) {
    val lecturerRepository: LecturerRepository = get()

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
        val lecturers by lecturerRepository.allLecturersFlow.collectAsState(emptyList())

        LazyColumn {
            items(lecturers) { lecturer ->
                Row {
                    Text(text = lecturer.name)
                }
            }

            item {
                AddLecturerItem()
            }
        }
    }
}

@Composable
private fun AddLecturerItem() {
    val (inAddMode, setInAddMode) = remember { mutableStateOf(false) }

    if (inAddMode) {
        val name = remember { mutableStateOf(TextFieldValue("")) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LabeledTextField(
                modifier = Modifier.weight(1f),
                text = name,
                label = "",
                placeholder = "",
            )

            IconButton(
                onClick = {
                    setInAddMode(false)
                },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth(),
            propagateMinConstraints = true,
        ) {
            IconButton(
                onClick = {
                    setInAddMode(true)
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