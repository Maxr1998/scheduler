package de.uaux.scheduler.ui.screens.management

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.util.l

@Composable
fun <T> EventListContent(
    events: List<T>,
    fabIcon: ImageVector,
    onAdd: () -> Unit,
    eventContent: @Composable (T) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (events.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp),
                        text = l("event_panel_header"),
                        color = MaterialTheme.colors.secondary,
                        style = MaterialTheme.typography.caption,
                    )
                }
                items(events) { event ->
                    eventContent(event)
                }
            }
        } else {
            CenteredTextMessage(
                text = l("event_panel_no_events"),
            )
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
            onClick = { onAdd() },
        ) {
            Icon(
                imageVector = fabIcon,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun CenteredTextMessage(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
        )
    }
}