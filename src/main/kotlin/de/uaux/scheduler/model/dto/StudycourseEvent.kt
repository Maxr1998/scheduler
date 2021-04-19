package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Event

@Immutable
data class StudycourseEvent(
    val event: Event,
    val semester: Int,
    val required: Boolean,
)