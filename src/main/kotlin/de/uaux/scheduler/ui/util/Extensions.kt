package de.uaux.scheduler.ui.util

import androidx.compose.foundation.border
import androidx.compose.material.Colors
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp

val Colors.highlight
    get() = onSurface.copy(alpha = 0.02f).compositeOver(surface)

fun Modifier.debugBorder(): Modifier =
    border(Dp.Hairline, SolidColor(Color.Red), RectangleShape)