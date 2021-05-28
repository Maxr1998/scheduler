package de.uaux.scheduler.ui.screens.management.event.suggestion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.repository.ScheduleRepository
import de.uaux.scheduler.repository.SuggestionRepository
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selected
import de.uaux.scheduler.ui.model.Selection
import de.uaux.scheduler.ui.util.LabeledTextField
import de.uaux.scheduler.ui.util.PopupDialog
import de.uaux.scheduler.ui.util.l
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get

@Composable
fun EditSuggestionDialog(event: Event, onDismissRequest: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val scheduleRepository: ScheduleRepository = get()
    val suggestionRepository: SuggestionRepository = get()

    PopupDialog(
        title = l("dialog_title_suggestions"),
        onDismissRequest = onDismissRequest,
        actions = {
            TextButton(
                onClick = onDismissRequest,
            ) {
                Text(text = l("button_text_close"))
            }
        },
    ) {
        var semesterSelection: Selection<Semester> by remember { mutableStateOf(Loading) }

        // Initial semester selection
        LaunchedEffect(Unit) {
            semesterSelection = Selection.fromNullable(scheduleRepository.allSemestersFlow.first().firstOrNull())
        }

        Column {
            SemesterSelection(
                current = semesterSelection.orNull(),
                onSelect = { semester ->
                    semesterSelection = Selection(semester)
                },
            )

            val semester = (semesterSelection as? Selected<Semester>)?.value
            if (semester != null) {
                var selectedSuggestion: Selection<Suggestion> by remember { mutableStateOf(Loading) }
                val inEditMode = remember { mutableStateOf(false) }

                LaunchedEffect(semester) {
                    val queryResult = withContext(Dispatchers.IO) {
                        suggestionRepository.querySuggestionBySemesterAndEvent(semester, event)
                    }
                    selectedSuggestion = Selection.fromNullable(queryResult)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Divider()

                when {
                    selectedSuggestion is Selected || inEditMode.value -> SuggestionDetails(
                        initialText = selectedSuggestion.orNull()?.text.orEmpty(),
                        initialConstraints = selectedSuggestion.orNull()?.constraints.orEmpty(),
                        inEditMode = inEditMode,
                        onSave = { text, constraints ->
                            coroutineScope.launch {
                                val updated = selectedSuggestion.orNull()?.copy(text = text, constraints = constraints)
                                val suggestion = updated ?: Suggestion(-1, semester, event, text, constraints)
                                val id = suggestionRepository.insertOrUpdate(suggestion)
                                selectedSuggestion = Selection(suggestion.copy(id = id))
                                inEditMode.value = false
                            }
                        },
                    )
                    selectedSuggestion == None -> AddSuggestionButton(
                        onClick = { inEditMode.value = true },
                    )
                    selectedSuggestion == Loading -> Box(
                        modifier = Modifier.height(64.dp).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionDetails(
    initialText: String,
    initialConstraints: List<Suggestion.Constraint>,
    inEditMode: MutableState<Boolean>? = null,
    onSave: (String, List<Suggestion.Constraint>) -> Unit = { _, _ -> },
) {
    val text = remember { mutableStateOf(TextFieldValue(initialText)) }
    val constraints = remember { initialConstraints.toMutableStateList() }

    if (inEditMode != null) {
        Spacer(modifier = Modifier.height(12.dp))
    }

    Row(
        modifier = Modifier.height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = l("section_label_suggestion"),
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.subtitle1,
        )

        if (inEditMode != null) {
            SuggestionEditButtons(
                inEditMode = inEditMode.value,
                onStart = {
                    inEditMode.value = true
                },
                onFinish = {
                    onSave(text.value.text, constraints.toList())
                },
                onCancel = {
                    text.value = TextFieldValue(initialText)
                    constraints.clear()
                    constraints.addAll(initialConstraints)
                    inEditMode.value = false
                },
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    LabeledTextField(
        modifier = Modifier.height(400.dp),
        text = text,
        label = l("input_label_suggestion_text"),
        placeholder = l("input_hint_suggestion_text"),
        singleLine = false,
        readOnly = inEditMode?.value != true,
    )
}

@Composable
private fun SuggestionEditButtons(
    inEditMode: Boolean,
    onStart: () -> Unit,
    onFinish: () -> Unit,
    onCancel: () -> Unit,
) {
    if (inEditMode) {
        IconButton(onClick = onFinish) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
            )
        }
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
            )
        }
    } else {
        IconButton(onClick = onStart) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun AddSuggestionButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = l("button_add_suggestion"))
    }
}