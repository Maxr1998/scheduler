package de.uaux.scheduler.ui.screens.management.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Lecturer
import de.uaux.scheduler.repository.LecturerRepository
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterialApi::class)
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
        val coroutineScope = rememberCoroutineScope()
        val lecturers by lecturerRepository.allLecturersFlow.collectAsState(emptyList())

        LazyColumn {
            item {
                AddLecturerItem(
                    onSave = { name ->
                        coroutineScope.launch {
                            lecturerRepository.insert(name)
                        }
                    },
                )
            }

            item {
                Divider()
            }

            items(lecturers, key = Lecturer::id) { lecturer ->
                ListItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(text = lecturer.name)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddLecturerItem(onSave: (String) -> Unit) {
    val (inAddMode, setInAddMode) = remember { mutableStateOf(false) }

    if (inAddMode) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val name = remember { mutableStateOf(TextFieldValue("")) }

            LabeledTextField(
                modifier = Modifier.weight(1f),
                text = name,
                label = l("input_label_lecturer_name"),
                placeholder = l("input_hint_lecturer_name"),
            )

            Box(
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
            ) {
                IconButton(
                    enabled = name.value.text.isNotBlank(),
                    onClick = {
                        onSave(name.value.text)
                        setInAddMode(false)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                    )
                }
            }
        }
    } else {
        ListItem(
            modifier = Modifier.clickable {
                setInAddMode(true)
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                )
            },
            text = {
                Text(text = l("button_add_lecturer"))
            },
        )
    }
}