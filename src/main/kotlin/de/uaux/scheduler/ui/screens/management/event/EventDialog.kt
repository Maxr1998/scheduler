package de.uaux.scheduler.ui.screens.management.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Lecturer
import de.uaux.scheduler.model.dto.EventType
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.LecturerRepository
import de.uaux.scheduler.ui.util.Chip
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.SaveButton
import de.uaux.scheduler.ui.util.SearchableSelectionDropdown
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.ui.util.parseNumberInput
import de.uaux.scheduler.viewmodel.TimetableViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun EventDialog(event: Event?, onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val eventRepository: EventRepository = get()
    val lecturerRepository: LecturerRepository = get()
    val eventName = remember { mutableStateOf(TextFieldValue(event?.name.orEmpty())) }
    val eventType = remember { mutableStateOf(event?.type?.let { i -> EventType.values()[i] } ?: EventType.UNDEFINED) }
    val eventModule = remember { mutableStateOf(TextFieldValue(event?.module.orEmpty())) }
    val eventDurationText = remember { mutableStateOf(TextFieldValue(event?.duration?.toString().orEmpty())) }
    val eventDuration by derivedStateOf {
        parseNumberInput(
            eventDurationText.value.text,
            0L..TimetableViewModel.MAX_MINUTES_IN_DAY,
            "input_error_invalid_duration",
        )
    }

    val eventParticipantsText = remember { mutableStateOf(TextFieldValue(event?.participants?.toString().orEmpty())) }
    val eventParticipants by derivedStateOf {
        parseNumberInput(
            eventParticipantsText.value.text,
            1..100000L,
            "input_error_invalid_participant_count",
        )
    }

    PopupDialog(
        title = l(if (event == null) "dialog_title_add_event" else "dialog_title_edit_event"),
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_cancel"))
            }

            SaveButton(
                enabled = eventName.value.text.isNotBlank() &&
                    eventDuration.value != null && eventDuration.error == null &&
                    eventParticipants.error == null,
                onSave = {
                    coroutineScope.launch {
                        val updated = Event(
                            event?.id ?: -1L,
                            eventName.value.text.trim(),
                            eventType.value.ordinal,
                            eventModule.value.text.trim(),
                            eventDuration.value!!.toInt(),
                            eventParticipants.value?.toInt(),
                        )
                        eventRepository.insertOrUpdate(updated)
                        onDismissRequest()
                    }
                }
            )
        }
    ) {
        Column {
            LabeledTextField(
                text = eventName,
                label = l("input_label_event_name"),
                placeholder = l("input_hint_event_name"),
            )

            Spacer(modifier = Modifier.height(4.dp))

            LabeledTextField(
                text = eventModule,
                label = l("input_label_event_module"),
                placeholder = l("input_hint_event_module"),
            )

            Spacer(modifier = Modifier.height(4.dp))

            LabeledTextField(
                text = eventDurationText,
                label = l("input_label_event_duration"),
                placeholder = l("input_hint_event_duration"),
                errorMessage = eventDuration.error?.let { error -> l(error) },
            )

            Spacer(modifier = Modifier.height(4.dp))

            LabeledTextField(
                text = eventParticipantsText,
                label = l("input_label_event_participants"),
                placeholder = l("input_hint_event_participants"),
                errorMessage = eventParticipants.error?.let { error -> l(error) },
            )

            if (event != null) {
                Divider()

                Box(
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                ) {
                    val contentColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                    CompositionLocalProvider(
                        LocalContentColor provides contentColor,
                        LocalContentAlpha provides contentColor.alpha,
                    ) {
                        Text(
                            text = l("chip_row_label_lecturers"),
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }

                val eventLecturers by lecturerRepository.queryLecturersByEventAsFlow(event).collectAsState(emptyList())
                val (inAddMode, setInAddMode) = remember { mutableStateOf(false) }

                LazyRow(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    item {
                        Chip(
                            text = null,
                            icon = Icons.Outlined.Add,
                            onClickIcon = {
                                setInAddMode(true)
                            },
                        )
                    }

                    items(eventLecturers, key = Lecturer::id) { lecturer ->
                        Chip(
                            text = {
                                Text(
                                    text = lecturer.name,
                                    style = MaterialTheme.typography.body2,
                                )
                            },
                            icon = Icons.Outlined.Clear,
                            onClickIcon = {
                                coroutineScope.launch {
                                    lecturerRepository.removeLecturerFromEvent(lecturer, event)
                                }
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (inAddMode) {
                    val searchQuery = remember { mutableStateOf("") }

                    val lecturers: List<Lecturer> by produceState(emptyList(), searchQuery.value, eventLecturers) {
                        value = lecturerRepository.searchByName(searchQuery.value, event)
                    }

                    SearchableSelectionDropdown(
                        label = l("input_label_event_add_lecturer"),
                        placeholder = l("input_hint_event_add_lecturer"),
                        onSearch = { query ->
                            searchQuery.value = query
                        },
                        searchResults = lecturers,
                        onSelect = { selectedLecturer ->
                            coroutineScope.launch {
                                lecturerRepository.addLecturerToEvent(selectedLecturer, event)
                            }
                        },
                        selected = null,
                        itemKey = Lecturer::id,
                        itemLabel = Lecturer::name
                    ) { lecturer ->
                        Text(text = lecturer.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}