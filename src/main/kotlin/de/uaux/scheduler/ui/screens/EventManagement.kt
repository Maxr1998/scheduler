package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.ui.model.DialogState
import de.uaux.scheduler.ui.screens.management.EventListContent
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.ThemedIconButton
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.DialogViewModel
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

@Composable
fun EventManagementScreen() = Column {
    Toolbar(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        title = l("screen_event_management"),
    ) {}

    DisableSelection {
        EventManagementScreenContent()
    }
}

@Composable
fun EventManagementScreenContent() {
    val eventManagementViewModel: EventManagementViewModel = get()
    val events by eventManagementViewModel.eventsFlow.collectAsState(emptyList())

    val dialogViewModel: DialogViewModel = get()
    val openSuggestionDialog: (Event) -> Unit = { event ->
        dialogViewModel.openDialog(DialogState.EditSuggestionDialog(event))
    }
    val openEventDialog: (Event?) -> Unit = { event ->
        dialogViewModel.openDialog(DialogState.EventDialog(event))
    }

    EventListContent(
        events = events,
        fabIcon = Icons.Outlined.Add,
        onAdd = { openEventDialog(null) },
    ) { event ->
        EventListItem(
            event = event,
            onClickSuggestion = openSuggestionDialog,
            onClickEdit = openEventDialog,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EventListItem(
    modifier: Modifier = Modifier,
    event: Event,
    onClickSuggestion: (Event) -> Unit,
    onClickEdit: (Event) -> Unit,
) {
    ListItem(
        modifier = modifier,
        text = {
            Text(text = event.name)
        },
        trailing = {
            Row {
                ThemedIconButton(
                    icon = Icons.Outlined.AutoAwesome,
                    onClick = { onClickSuggestion(event) },
                )

                Spacer(modifier = Modifier.width(4.dp))

                EditButton(
                    onClick = { onClickEdit(event) },
                )
            }
        },
    )
}