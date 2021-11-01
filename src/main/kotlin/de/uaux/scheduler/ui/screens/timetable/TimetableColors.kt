package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.compositeOver
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.dto.ScheduledEvent
import java.util.concurrent.atomic.AtomicInteger

class TimetableColors(private val surfaceColor: Color) {
    private val hue = AtomicInteger(0)
    private val colors: MutableMap<Event, Color> = HashMap()

    @OptIn(ExperimentalGraphicsApi::class)
    private fun nextColor(): Color = Color.hsv(
        hue = hue.updateAndGet { hue -> (hue + 47) % 360 }.toFloat(),
        saturation = 1f,
        value = 1f,
        alpha = 0.1f,
        colorSpace = ColorSpaces.LinearSrgb,
    ).compositeOver(surfaceColor)

    operator fun get(event: Event): Color = colors.computeIfAbsent(event) { nextColor() }
    operator fun get(scheduledEvent: ScheduledEvent): Color = get(scheduledEvent.event)
}