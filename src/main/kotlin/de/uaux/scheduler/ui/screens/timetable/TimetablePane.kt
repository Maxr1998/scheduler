@file:Suppress("NOTHING_TO_INLINE")

package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.duration
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.util.DraggableCard
import de.uaux.scheduler.ui.util.ZIndex
import de.uaux.scheduler.ui.util.highlight
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.util.formatMinutesOfDay
import de.uaux.scheduler.viewmodel.TimetableViewModel
import mu.KotlinLogging
import org.koin.androidx.compose.get
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

const val TIMESLOT_SNAP_MINUTES = 10

@Composable
fun TimetablePane(modifier: Modifier = Modifier) {
    val timetableViewModel: TimetableViewModel = get()
    val events = timetableViewModel.events
    val showWeekend by timetableViewModel.showWeekend
    val numDays = if (showWeekend == ShowWeekend.FALSE) 5 else 7
    val dayRange by timetableViewModel.dayRange
    val timeslots by timetableViewModel.timeslots

    var pointerOffset by remember { mutableStateOf(Offset.Zero) }
    val draggedEvent = remember { mutableStateOf<ScheduledEvent?>(null) }
    var dropLocation by remember { mutableStateOf<DropLocation?>(null) }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp),
        ) {
            for (day in DayOfWeek.values().take(numDays)) {
                DayOfWeekLabel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    day = day,
                )
            }
        }
        Divider(modifier = Modifier.zIndex(ZIndex.DIVIDER))

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    while (true) {
                        awaitPointerEventScope {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            event.changes.lastOrNull()?.let { change ->
                                pointerOffset = change.position
                            }
                        }
                    }
                },
        ) {
            val columnWidth = constraints.maxWidth.toFloat() / numDays
            val columnHeight = constraints.maxHeight.toFloat()
            val minuteHeight = columnHeight / (dayRange.last - dayRange.first)

            // Draw timeslots
            val checkerboardColor = MaterialTheme.colors.highlight
            for (timeslot in timeslots) {
                if (timeslot !in dayRange) continue
                val offset = Offset(0f, minuteHeight * (timeslot.start_time - dayRange.first))
                Box(
                    modifier = Modifier
                        .zIndex(ZIndex.BACKGROUND)
                        .layout { measurable, _ ->
                            simpleLayout(measurable, constraints.maxWidth.toFloat(), minuteHeight * timeslot.duration, offset)
                        }
                        .background(checkerboardColor, RectangleShape)
                )
            }

            // Draw drop indicator for dragged events, snapping to days and slots
            draggedEvent.value?.let { event ->
                val layoutModifier = remember(
                    pointerOffset,
                    columnWidth, columnHeight, minuteHeight,
                    dayRange, numDays,
                    timeslots, event,
                ) {
                    // Calculate hovered day
                    val day = (0 until numDays).first { day ->
                        val offset = pointerOffset.x.coerceIn(0f, columnWidth * numDays)
                        offset >= day * columnWidth && offset <= (day + 1) * columnWidth
                    }

                    // Calculate hovered start time in minutes
                    val height = minuteHeight * event.duration
                    val rawOffset = pointerOffset.y - height / 2
                    val rawMinutes = dayRange.first + rawOffset / minuteHeight
                    val timeslotOffset = timeslots.find { timeslot ->
                        abs(rawMinutes - timeslot.start_time) <= TIMESLOT_SNAP_MINUTES
                    }?.let { timeslot ->
                        minuteHeight * (timeslot.start_time - dayRange.first)
                    }
                    val offsetY = (timeslotOffset ?: rawOffset).coerceIn(0f, columnHeight - height)
                    val startTime = (dayRange.first + offsetY / minuteHeight).roundToInt()

                    // Update drop location
                    dropLocation = DropLocation(DayOfWeek.of(day + 1), startTime)

                    // Return layout modifier
                    Modifier.layout { measurable, _ ->
                        simpleLayout(measurable, columnWidth, height, Offset(columnWidth * day, offsetY))
                    }
                }

                IndicatorCard(modifier = layoutModifier)
            }

            // Draw events
            for (event in events) {
                key(event) {
                    val layoutModifier = remember(event, columnWidth, minuteHeight, dayRange) {
                        Modifier.layout { measurable, _ ->
                            val offset = Offset(columnWidth * (event.day.value - 1), minuteHeight * (event.startTime - dayRange.first))
                            simpleLayout(measurable, columnWidth, minuteHeight * event.duration, offset)
                        }
                    }
                    TimetableEventCard(
                        modifier = layoutModifier,
                        event = event,
                        onDragStart = {
                            draggedEvent.value = event
                        },
                        onDrop = { _, persist ->
                            val droppedEvent = draggedEvent.value ?: return@TimetableEventCard
                            logger.debug { "Dropped $droppedEvent at $dropLocation" + ", persisting".takeIf { persist } }
                            if (persist && dropLocation != null) {
                                val (day, startTime) = dropLocation!!
                                timetableViewModel.reschedule(droppedEvent, day, startTime)
                            }
                            draggedEvent.value = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayOfWeekLabel(modifier: Modifier = Modifier, day: DayOfWeek) {
    Box(
        modifier = modifier.padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = remember(day) { day.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Composable
private fun TimetableEventCard(
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
private fun IndicatorCard(
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

// Helper functions
private inline operator fun IntRange.contains(timeslot: Timeslot): Boolean =
    timeslot.start_time >= first && timeslot.end_time <= last

// Layout helper
private fun MeasureScope.simpleLayout(measurable: Measurable, width: Float, height: Float, offset: Offset): MeasureResult {
    val placeable = measurable.measure(Constraints.fixed(width.toInt(), height.toInt()))
    return layout(placeable.width, placeable.height) {
        placeable.place(offset.round())
    }
}

@Immutable
private data class DropLocation(
    val day: DayOfWeek,
    val minutes: Int,
) {
    override fun toString(): String = "$day / ${formatMinutesOfDay(minutes)}"
}