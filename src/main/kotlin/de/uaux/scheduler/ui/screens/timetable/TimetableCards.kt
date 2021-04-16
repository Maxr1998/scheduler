package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.ui.util.DraggableCard
import de.uaux.scheduler.ui.util.l

@Composable
fun TimetableEventCard(
    modifier: Modifier = Modifier,
    event: ScheduledEvent,
    onDragStart: () -> Unit = { },
    onDragUpdate: (offset: Offset) -> Unit = { },
    onDrop: (offset: Offset, persist: Boolean) -> Unit = { _, _ -> },
) {
    DraggableCard(
        modifier = modifier,
        onDragStart = onDragStart,
        onDragUpdate = onDragUpdate,
        onDrop = onDrop
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = event.event.name,
                fontSize = 14.sp,
                style = MaterialTheme.typography.subtitle1,
            )
            event.room?.let { room ->
                Text(
                    text = l("event_label_room") + ": ${room.name}",
                    fontSize = 11.sp,
                    style = MaterialTheme.typography.subtitle2,
                )
            }

        }
    }
}

@Composable
fun IndicatorCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(color.copy(alpha = 0.12f), MaterialTheme.shapes.medium)
            .border(1.dp, color, MaterialTheme.shapes.medium),
    )
}

@Composable
fun UnscheduledEventCard(event: Event) {
    DraggableCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = event.name,
                fontSize = 14.sp,
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}