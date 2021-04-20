@file:Suppress("NOTHING_TO_INLINE")

package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.zIndex
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.duration
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.model.TimetableSelection
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.ZIndex
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.ui.util.lightenedBackground
import de.uaux.scheduler.util.formatMinutesOfDay
import de.uaux.scheduler.util.size
import de.uaux.scheduler.viewmodel.TimetableViewModel
import mu.KotlinLogging
import org.koin.androidx.compose.get
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

const val TIMESLOT_SNAP_MINUTES = 10

@Composable
fun TimetableScreenContent(selection: TimetableSelection.Loaded) {
    val timetableViewModel: TimetableViewModel = get()
    val showWeekend by timetableViewModel.showWeekend
    val numDays = if (showWeekend == ShowWeekend.FALSE) 5 else 7

    val pointerOffset = remember { mutableStateOf(Offset.Zero) }
    val draggedEvent = remember { mutableStateOf<ScheduledEvent?>(null) }
    val dropLocation = remember { mutableStateOf<DropLocation?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        event.changes.lastOrNull()?.let { change ->
                            pointerOffset.value = change.position
                        }
                    }
                }
            },
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp),
        ) {
            for (day in DayOfWeek.values().take(numDays)) {
                WeightedTextBox(
                    text = remember(day) { day.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
                )
            }
            WeightedTextBox(text = l("event_panel_header"))
        }

        Divider(modifier = Modifier.zIndex(ZIndex.DIVIDER))

        // Timetable content and unscheduled events
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            TimetablePane(
                modifier = Modifier.weight(numDays.toFloat(), true).fillMaxHeight(),
                numDays = numDays,
                pointerOffset = pointerOffset.value,
                draggedEvent = draggedEvent.value?.event,
                dropLocation = dropLocation,
                onDrag = { event ->
                    draggedEvent.value = event
                },
                onDrop = { persist ->
                    val droppedEvent = draggedEvent.value ?: return@TimetablePane
                    val location = dropLocation.value ?: return@TimetablePane

                    logger.debug("Dropped $droppedEvent at $location" + ", persisting".takeIf { persist })
                    if (persist) {
                        timetableViewModel.reschedule(droppedEvent, location.day, location.minutes)
                    }

                    draggedEvent.value = null
                }
            )
            VerticalDivider(modifier = Modifier.zIndex(ZIndex.DIVIDER))
            UnscheduledPane(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onDragStart = { studycourseEvent ->
                    draggedEvent.value = ScheduledEvent(selection.semester, studycourseEvent, DayOfWeek.MONDAY, 0, null)
                },
                onDrop = { persist ->
                    val droppedEvent = draggedEvent.value ?: return@UnscheduledPane
                    val location = dropLocation.value ?: return@UnscheduledPane

                    logger.debug("Dropped ${droppedEvent.toShortString()} at $location" + ", persisting".takeIf { persist })
                    if (persist) {
                        timetableViewModel.schedule(droppedEvent.copy(day = location.day, startTime = location.minutes))
                    }

                    draggedEvent.value = null
                },
            )
        }
    }
}

@Composable
private fun TimetablePane(
    modifier: Modifier = Modifier,
    numDays: Int,
    pointerOffset: Offset,
    draggedEvent: Event?,
    dropLocation: MutableState<DropLocation?>,
    onDrag: (ScheduledEvent) -> Unit,
    onDrop: (success: Boolean) -> Unit,
) {
    val timetableViewModel: TimetableViewModel = get()
    val events = timetableViewModel.events
    val dayRange by timetableViewModel.dayRange
    val timeslots by timetableViewModel.timeslots

    BoxWithConstraints(modifier = modifier) {
        val columnWidth = constraints.maxWidth.toFloat() / numDays
        val columnHeight = constraints.maxHeight.toFloat()
        val minuteHeight = columnHeight / (dayRange.last - dayRange.first)

        // Draw timeslots
        val checkerboardColor = MaterialTheme.colors.lightenedBackground
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

        // Update drop location and draw drop indicator
        if (draggedEvent != null) {
            val (location, layoutModifier) = calculateHoverLocation(
                pointerOffset,
                columnWidth, columnHeight, minuteHeight,
                dayRange, numDays,
                timeslots,
                draggedEvent.duration,
            )

            dropLocation.value = location

            if (layoutModifier != null) {
                IndicatorCard(modifier = layoutModifier)
            }
        }

        // Draw events
        var overlap = -1..-1
        for (i in events.indices) {
            val event = events[i]
            key(event) {
                // Calculate offset multiplier (and overlap if necessary)
                val offsetMultiplier: Int
                if (i in overlap) {
                    // Event is already part of an active overlap group, set offset according to the provided data
                    offsetMultiplier = i - overlap.first
                } else {
                    // Check for new overlap groups
                    var maxEndTime = event.endTime
                    var parallel = 0

                    // Iterate through successive events to check for overlaps
                    for (j in i + 1 until events.size) {
                        val successor = events[j]
                        // Increase counter for events on the same day that start before the current overlap group ends,
                        // otherwise break
                        if (successor.day == event.day && successor.startTime < maxEndTime) parallel++ else break
                        maxEndTime = max(maxEndTime, successor.endTime)
                    }

                    // Overlap is from current event to the last overlapping one
                    overlap = i..i + parallel

                    // First event in overlap group never has an offset
                    offsetMultiplier = 0
                }

                // Set size divisor according to computed data
                val sizeDivisor = overlap.size + 1

                val layoutModifier = Modifier.layout { measurable, _ ->
                    val offset = Offset(columnWidth * (event.day.value - 1) + ((columnWidth / sizeDivisor) * offsetMultiplier), minuteHeight * (event.startTime - dayRange.first))
                    simpleLayout(measurable, columnWidth / sizeDivisor, minuteHeight * event.duration, offset)
                }
                TimetableEventCard(
                    modifier = layoutModifier,
                    event = event,
                    onDrag = {
                        onDrag(event)
                    },
                    onDrop = onDrop,
                )
            }
        }
    }
}

@Composable
private fun UnscheduledPane(
    modifier: Modifier = Modifier,
    onDragStart: (StudycourseEvent) -> Unit,
    onDrop: (success: Boolean) -> Unit,
) {
    val timetableViewModel: TimetableViewModel = get()
    LazyColumn(
        modifier = modifier,
    ) {
        items(timetableViewModel.unscheduledEvents, key = { studycourseEvent -> studycourseEvent.event.id }) { unscheduled ->
            UnscheduledEventCard(
                studycourseEvent = unscheduled,
                onDragStart = {
                    onDragStart(unscheduled)
                },
                onDrop = onDrop,
            )
        }
    }
}

@Composable
private fun RowScope.WeightedTextBox(text: String) {
    Box(
        modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

// Helper functions
private inline operator fun IntRange.contains(timeslot: Timeslot): Boolean =
    timeslot.start_time >= first && timeslot.end_time <= last

/**
 * Calculates the [DropLocation] for the hovered spot and a [Modifier]
 * to position the drop indicator that snaps to days and slots
 */
@Stable
private fun calculateHoverLocation(
    pointerOffset: Offset,
    columnWidth: Float,
    columnHeight: Float,
    minuteHeight: Float,
    dayRange: IntRange,
    numDays: Int,
    timeslots: List<Timeslot>,
    eventDuration: Int,
): Pair<DropLocation?, Modifier?> {
    // Abort drag over unscheduled events panel
    if (pointerOffset.x > columnWidth * numDays) {
        return null to null
    }

    // Calculate hovered day
    val day = (0 until numDays).first { day ->
        val offset = pointerOffset.x.coerceAtLeast(0f)
        offset >= day * columnWidth && offset <= (day + 1) * columnWidth
    }

    // Calculate hovered start time in minutes
    val height = minuteHeight * eventDuration
    val rawOffset = pointerOffset.y - height / 2
    val rawMinutes = dayRange.first + rawOffset / minuteHeight
    val timeslotOffset = timeslots.find { timeslot ->
        abs(rawMinutes - timeslot.start_time) <= TIMESLOT_SNAP_MINUTES
    }?.let { timeslot ->
        minuteHeight * (timeslot.start_time - dayRange.first)
    }
    val offsetY = (timeslotOffset ?: rawOffset).coerceIn(0f, columnHeight - height)
    val startTime = (dayRange.first + offsetY / minuteHeight).roundToInt()

    // Return result
    val dropLocation = DropLocation(DayOfWeek.of(day + 1), startTime)
    val layoutModifier = Modifier.layout { measurable, _ ->
        simpleLayout(measurable, columnWidth, height, Offset(columnWidth * day, offsetY))
    }

    return dropLocation to layoutModifier
}

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