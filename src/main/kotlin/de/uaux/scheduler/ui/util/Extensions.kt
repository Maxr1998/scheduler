package de.uaux.scheduler.ui.util

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.compositeOver

val Colors.highlight
    get() = onSurface.copy(alpha = 0.02f).compositeOver(surface)