package de.uaux.scheduler.ui.screens.management.event

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
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.SaveButton
import de.uaux.scheduler.ui.util.calculateNumberInputError
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.TimetableViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun EventDialog(event: Event?, onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val eventRepository: EventRepository = get()
    val eventName = remember { mutableStateOf(TextFieldValue(event?.name.orEmpty())) }
    val eventModule = remember { mutableStateOf(TextFieldValue(event?.module.orEmpty())) }
    val eventDurationText = remember { mutableStateOf(TextFieldValue(event?.duration?.toString().orEmpty())) }
    val (eventDuration, eventDurationError) = calculateNumberInputError(
        eventDurationText.value.text,
        0L..TimetableViewModel.MAX_MINUTES_IN_DAY,
        "input_error_invalid_duration",
    )
    val eventParticipantsText = remember { mutableStateOf(TextFieldValue(event?.participants?.toString().orEmpty())) }
    val (eventParticipants, eventParticipantsError) = calculateNumberInputError(
        eventParticipantsText.value.text,
        1..100000L,
        "input_error_invalid_participant_count",
    )

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
                    eventDuration != null && eventDurationError == null &&
                    eventParticipantsError == null,
                onSave = {
                    coroutineScope.launch {
                        val updated = Event(
                            event?.id ?: -1L,
                            eventName.value.text.trim(),
                            eventModule.value.text.trim(),
                            eventDuration!!.toInt(),
                            eventParticipants?.toInt(),
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
                errorMessage = eventDurationError,
            )

            Spacer(modifier = Modifier.height(4.dp))

            LabeledTextField(
                text = eventParticipantsText,
                label = l("input_label_event_participants"),
                placeholder = l("input_hint_event_participants"),
                errorMessage = eventParticipantsError,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}