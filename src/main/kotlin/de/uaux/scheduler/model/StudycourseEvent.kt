package de.uaux.scheduler.model

import androidx.compose.runtime.Immutable

@Immutable
data class StudycourseEvent(
    val event: Event,
    val semester: Int?,
    val required: Boolean,
)