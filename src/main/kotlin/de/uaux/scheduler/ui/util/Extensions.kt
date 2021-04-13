package de.uaux.scheduler.ui.util

import androidx.compose.foundation.border
import androidx.compose.material.Colors
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp

val Colors.lightenedBackground
    get() = if (isLight) Color(0xFFFEFEFE) else Color(0xFF202020)

fun Modifier.debugBorder(): Modifier =
    border(Dp.Hairline, SolidColor(Color.Red), RectangleShape)