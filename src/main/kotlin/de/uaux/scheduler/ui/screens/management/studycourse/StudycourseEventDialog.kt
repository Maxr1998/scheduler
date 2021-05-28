package de.uaux.scheduler.ui.screens.management.studycourse

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.SaveButton
import de.uaux.scheduler.ui.util.SearchableSelectionDropdown
import de.uaux.scheduler.ui.util.ToggleableText
import de.uaux.scheduler.ui.util.disabled
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun StudycourseEventDialog(
    studycourse: Studycourse,
    studycourseEvent: StudycourseEvent?,
    onCreateEventRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val studycourseRepository: StudycourseRepository = get()
    val eventRepository: EventRepository = get()
    val event = remember { mutableStateOf(studycourseEvent?.event) }
    val semesterText = remember { mutableStateOf(TextFieldValue(studycourseEvent?.semester?.toString().orEmpty())) }
    val semester = semesterText.value.text.trim().toIntOrNull()
    val semesterError = when {
        semesterText.value.text.isBlank() -> null
        semester == null -> l("input_error_only_numbers")
        semester !in 1..10 -> l("input_error_invalid_semester")
        else -> null
    }

    val required = remember { mutableStateOf(studycourseEvent?.required ?: false) }

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
                    Text(text = l("button_text_remove"))
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_cancel"))
            }

            SaveButton(
                enabled = event.value != null && semester != null && semesterError == null,
                onSave = {
                    coroutineScope.launch {
                        val updated = StudycourseEvent(
                            requireNotNull(event.value),
                            requireNotNull(semester),
                            required.value,
                        )
                        studycourseRepository.link(studycourse, updated)
                        onDismissRequest()
                    }
                }
            )
        }
    ) {
        Column {
            if (studycourseEvent == null) { // Select new event
                val searchQuery = remember { mutableStateOf("") }

                val createNewPseudoEvent = Event(-1, l("event_title_add_event"), 0, "", 0, null)
                val events: List<Event> by produceState(emptyList(), searchQuery.value) {
                    value = eventRepository.searchByName(searchQuery.value, studycourse) + createNewPseudoEvent
                }

                SearchableSelectionDropdown(
                    label = l("input_label_studycourse_event_event"),
                    placeholder = l("input_hint_studycourse_event_event"),
                    onSearch = { query ->
                        event.value = null
                        searchQuery.value = query
                    },
                    searchResults = events,
                    onSelect = { selectedEvent ->
                        when (selectedEvent) {
                            createNewPseudoEvent -> onCreateEventRequest()
                            else -> event.value = selectedEvent
                        }
                    },
                    selected = event.value,
                    itemLabel = Event::name
                ) { event ->
                    Text(text = event.name)
                }
            } else { // Event is already selected, show read-only
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = l("input_label_studycourse_event_event"))
                    },
                    value = studycourseEvent.event.name,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LabeledTextField(
                text = semesterText,
                label = l("input_label_studycourse_event_semester"),
                placeholder = l("input_hint_studycourse_event_semester"),
                errorMessage = semesterError,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ToggleableText(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, SolidColor(MaterialTheme.colors.disabled), MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium),
                state = required,
                text = l("studycourse_domain_voluntary"),
                checkedText = l("studycourse_domain_required")
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}