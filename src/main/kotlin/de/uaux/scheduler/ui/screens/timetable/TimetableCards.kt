package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.ui.util.DraggableCard
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.util.formatMinutesOfDay

private const val POINTER_KEY_PSEUDO_DRAGGABLE = "pseudo-draggable-card"

@Composable
fun TimetableEventCard(
    modifier: Modifier = Modifier,
    event: ScheduledEvent,
    onClick: () -> Unit,
    onDrag: () -> Unit,
    onDrop: (persist: Boolean) -> Unit,
) {
    DraggableCard(
        modifier = modifier,
        onClick = onClick,
        onDragStart = onDrag,
        onDragUpdate = { },
        onDrop = { _, success -> onDrop(success) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
        ) {
            Text(
                text = event.event.name,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = l("event_label_recommended_semester").format(event.studycourseEvent.semester),
                fontSize = 10.sp,
                style = MaterialTheme.typography.subtitle2,
            )

            event.room?.let { room ->
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${l("event_label_room")}: ${room.name}",
                    fontSize = 10.sp,
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
fun UnscheduledEventCard(
    studycourseEvent: StudycourseEvent,
    onClick: () -> Unit = { },
    onDragStart: () -> Unit = { },
    onDragUpdate: () -> Unit = { },
    onDrop: (success: Boolean) -> Unit = { },
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onClick)
            .pointerInput(POINTER_KEY_PSEUDO_DRAGGABLE) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart()
                    },
                    onDrag = { change, _ ->
                        change.consumeAllChanges()
                        onDragUpdate()
                    },
                    onDragEnd = {
                        onDrop(true)
                    },
                    onDragCancel = {
                        onDrop(false)
                    },
                )
            },
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = studycourseEvent.event.name,
                fontSize = 12.sp,
                style = MaterialTheme.typography.subtitle1,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${l("event_label_duration")}: ${formatMinutesOfDay(studycourseEvent.event.duration)}",
                fontSize = 10.sp,
                style = MaterialTheme.typography.subtitle2,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = l("event_label_recommended_semester").format(studycourseEvent.semester),
                fontSize = 10.sp,
                style = MaterialTheme.typography.subtitle2,
            )
        }
    }
}