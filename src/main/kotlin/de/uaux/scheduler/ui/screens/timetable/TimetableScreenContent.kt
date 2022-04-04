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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Timeslot
import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.UnscheduledEvent
import de.uaux.scheduler.model.duration
import de.uaux.scheduler.ui.model.DialogState
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.model.TimetableFilter
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.ZIndex
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.ui.util.lightenedBackground
import de.uaux.scheduler.util.formatTimeMinutesOfDay
import de.uaux.scheduler.util.size
import de.uaux.scheduler.viewmodel.DialogViewModel
import de.uaux.scheduler.viewmodel.TimetableViewModel
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.koin.androidx.compose.get
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

private val logger = KotlinLogging.logger {}

val timetablePaddingStart = 20.dp
private const val DAYS_PER_WEEK = 7
private const val DAYS_PER_WORKWEEK = 5
private const val TIMESLOT_SNAP_MINUTES = 10

@Composable
fun TimetableScreenContent(filter: TimetableFilter) {
    val coroutineScope = rememberCoroutineScope()
    val dialogViewModel: DialogViewModel = get()
    val timetableViewModel: TimetableViewModel = get()
    val showWeekend by timetableViewModel.showWeekend
    val numDays = if (showWeekend == ShowWeekend.FALSE) DAYS_PER_WORKWEEK else DAYS_PER_WEEK

    val pointerOffset = remember { mutableStateOf(Offset.Zero) }
    val draggedEvent = remember { mutableStateOf<ScheduledEvent?>(null) }
    val dropLocation = remember { mutableStateOf<DropLocation?>(null) }

    val showSuggestion: (StudycourseEvent, ScheduledEvent?) -> Unit = { studycourseEvent, scheduledEvent ->
        coroutineScope.launch {
            val suggestion = timetableViewModel.getSuggestion(filter.semester, studycourseEvent.event)
            dialogViewModel.openDialog(DialogState.EventDetailsDialog(studycourseEvent, scheduledEvent, suggestion))
        }
    }

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
        DayHeader(numDays = numDays)

        Divider(modifier = Modifier.zIndex(ZIndex.DIVIDER))

        // Timetable content and unscheduled events
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            Column(
                modifier = Modifier.weight(numDays.toFloat(), true).fillMaxHeight(),
            ) {
                TimetablePane(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    numDays = numDays,
                    pointerOffset = pointerOffset.value,
                    draggedEvent = draggedEvent.value?.event,
                    onUpdateDropLocation = { location ->
                        dropLocation.value = location
                    },
                    onClick = { event ->
                        showSuggestion(event.studycourseEvent, event)
                    },
                    onDrag = { event ->
                        draggedEvent.value = event
                    },
                    onDrop = { persist ->
                        val droppedEvent = draggedEvent.value ?: return@TimetablePane
                        val location = dropLocation.value ?: return@TimetablePane

                        if (location == DropLocation.REMOVE) {
                            // Remove from timetable when dropped over unscheduled events
                            logger.debug("Dropped $droppedEvent over unscheduled events" + ", persisting".takeIf { persist })

                            if (persist) {
                                timetableViewModel.unschedule(droppedEvent)
                            }
                        } else {
                            logger.debug("Dropped $droppedEvent at $location" + ", persisting".takeIf { persist })
                            if (persist) {
                                timetableViewModel.reschedule(droppedEvent, location.day, location.minutes)
                            }
                        }

                        draggedEvent.value = null
                    }
                )

                Divider()

                StatusLine(
                    status = buildAnnotatedString {
                        append("Idle.")
                        addStyle(SpanStyle(fontStyle = FontStyle.Italic), 0, length)
                    },
                )
            }
            VerticalDivider(modifier = Modifier.zIndex(ZIndex.DIVIDER))
            UnscheduledPane(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onClick = { unscheduledEvent ->
                    showSuggestion(unscheduledEvent.studycourseEvent, null)
                },
                onDragStart = { unscheduledEvent ->
                    draggedEvent.value = ScheduledEvent(filter.semester, unscheduledEvent.studycourseEvent, DayOfWeek.MONDAY, 0, null)
                },
                onDrop = { persist ->
                    val droppedEvent = draggedEvent.value ?: return@UnscheduledPane
                    val location = dropLocation.value ?: return@UnscheduledPane

                    if (location != DropLocation.REMOVE) {
                        logger.debug("Dropped ${droppedEvent.toShortString()} at $location" + ", persisting".takeIf { persist })
                        if (persist) {
                            timetableViewModel.schedule(droppedEvent.copy(day = location.day, startTime = location.minutes))
                        }
                    }

                    draggedEvent.value = null
                },
            )
        }
    }
}

@Composable
private fun DayHeader(numDays: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().height(32.dp),
    ) {
        Row(
            modifier = Modifier.weight(numDays.toFloat(), true).padding(start = timetablePaddingStart),
        ) {
            for (day in DayOfWeek.values().take(numDays)) {
                WeightedTextBox(
                    text = remember(day) { day.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
                )
            }
        }
        WeightedTextBox(text = l("timetable_unscheduled_events_header"))
    }
}

@Composable
private fun TimetablePane(
    modifier: Modifier = Modifier,
    numDays: Int,
    pointerOffset: Offset,
    draggedEvent: Event?,
    onUpdateDropLocation: (DropLocation) -> Unit,
    onClick: (ScheduledEvent) -> Unit,
    onDrag: (ScheduledEvent) -> Unit,
    onDrop: (success: Boolean) -> Unit,
) {
    val timetableViewModel: TimetableViewModel = get()
    val events = timetableViewModel.events
    val dayRange by timetableViewModel.dayRange
    val timeslots by timetableViewModel.timeslots

    BoxWithConstraints(modifier = modifier) {
        val paddingStart = with(LocalDensity.current) { timetablePaddingStart.toPx() }
        val columnWidth = (constraints.maxWidth.toFloat() - paddingStart) / numDays
        val columnHeight = constraints.maxHeight.toFloat()
        val minuteHeight = columnHeight / (dayRange.last - dayRange.first)

        // Draw timeslots
        val checkerboardColor = MaterialTheme.colors.lightenedBackground
        for (timeslot in timeslots) {
            if (timeslot !in dayRange) continue
            val height = minuteHeight * timeslot.duration
            val offset = Offset(0f, minuteHeight * (timeslot.start_time - dayRange.first))
            Box(
                modifier = Modifier
                    .zIndex(ZIndex.BACKGROUND)
                    .layout { measurable, _ ->
                        simpleLayout(measurable, constraints.maxWidth.toFloat(), height, offset)
                    }
                    .background(checkerboardColor, RectangleShape)
            ) {
                val timeslotTextHeight = with(LocalDensity.current) { height.toDp() }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(timeslotTextHeight, timetablePaddingStart)
                        .offset(-((timeslotTextHeight - timetablePaddingStart) / 2))
                        .rotate(@Suppress("MagicNumber") -90f)
                        .size(timetablePaddingStart, timeslotTextHeight)
                        .padding(4.dp),
                    text = "${formatTimeMinutesOfDay(timeslot.start_time)} - ${formatTimeMinutesOfDay(timeslot.end_time)}",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.subtitle2,
                )
            }
        }

        // Update drop location and draw drop indicator
        if (draggedEvent != null) {
            val (location, layoutModifier) = calculateHoverLocation(
                pointerOffset,
                paddingStart, columnWidth, columnHeight, minuteHeight,
                dayRange, numDays,
                timeslots,
                draggedEvent.duration,
            )

            onUpdateDropLocation(location)

            if (layoutModifier != null) {
                IndicatorCard(modifier = layoutModifier)
            }
        }

        // Draw events
        val surfaceColor = MaterialTheme.colors.surface
        val timetableColors = remember(surfaceColor) { TimetableColors(surfaceColor) }
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
                    val offset = Offset(paddingStart + columnWidth * (event.day.value - 1) + ((columnWidth / sizeDivisor) * offsetMultiplier), minuteHeight * (event.startTime - dayRange.first))
                    simpleLayout(measurable, columnWidth / sizeDivisor, minuteHeight * event.duration, offset)
                }
                TimetableEventCard(
                    modifier = layoutModifier,
                    backgroundColor = timetableColors[event],
                    event = event,
                    onClick = {
                        onClick(event)
                    },
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
private fun StatusLine(status: AnnotatedString) {
    Box(
        modifier = Modifier.fillMaxWidth().height(32.dp).padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
private fun UnscheduledPane(
    modifier: Modifier = Modifier,
    onClick: (UnscheduledEvent) -> Unit,
    onDragStart: (UnscheduledEvent) -> Unit,
    onDrop: (success: Boolean) -> Unit,
) {
    val timetableViewModel: TimetableViewModel = get()
    LazyColumn(
        modifier = modifier,
    ) {
        items(timetableViewModel.unscheduledEvents, key = { studycourseEvent -> studycourseEvent.studycourseEvent.event.id }) { unscheduled ->
            UnscheduledEventCard(
                unscheduledEvent = unscheduled,
                onClick = {
                    onClick(unscheduled)
                },
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
@Suppress("unused") // invalid warning
private inline operator fun IntRange.contains(timeslot: Timeslot): Boolean =
    timeslot.start_time >= first && timeslot.end_time <= last

/**
 * Calculates the [DropLocation] for the hovered spot and a [Modifier]
 * to position the drop indicator that snaps to days and slots
 */
@Stable
private fun calculateHoverLocation(
    pointerOffset: Offset,
    paddingStart: Float,
    columnWidth: Float,
    columnHeight: Float,
    minuteHeight: Float,
    dayRange: IntRange,
    numDays: Int,
    timeslots: List<Timeslot>,
    eventDuration: Int,
): Pair<DropLocation, Modifier?> {
    // Abort drag over unscheduled events panel
    if (pointerOffset.x > columnWidth * numDays) {
        return DropLocation.REMOVE to null
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
        simpleLayout(measurable, columnWidth, height, Offset(paddingStart + columnWidth * day, offsetY))
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
    override fun toString(): String = "$day / ${formatTimeMinutesOfDay(minutes)}"

    companion object {
        val REMOVE = DropLocation(DayOfWeek.MONDAY, Int.MIN_VALUE)
    }
}