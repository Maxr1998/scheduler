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
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

@Composable
fun EventDialog(event: Event?, onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val eventRepository: EventRepository = get()
    val eventName = remember { mutableStateOf(TextFieldValue(event?.name.orEmpty())) }
    val eventModule = remember { mutableStateOf(TextFieldValue(event?.module.orEmpty())) }
    val eventParticipantsText = remember { mutableStateOf(TextFieldValue(event?.participants?.toString().orEmpty())) }
    val eventParticipants = eventParticipantsText.value.text.trim().toLongOrNull()
    val eventParticipantsError = when {
        eventParticipantsText.value.text.isBlank() -> null
        eventParticipants == null -> l("input_error_only_numbers")
        eventParticipants !in 0..100000L -> l("input_error_invalid_participant_count")
        else -> null
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
                    eventModule.value.text.isNotBlank() &&
                    eventParticipantsError == null,
                onSave = {
                    coroutineScope.launch {
                        val updated = Event(
                            event?.id ?: -1L,
                            eventName.value.text,
                            eventModule.value.text,
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
                text = eventParticipantsText,
                label = l("input_label_event_participants"),
                placeholder = l("input_hint_event_participants"),
                errorMessage = eventParticipantsError,
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}