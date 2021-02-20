package de.uaux.scheduler.model

data class StudycourseEvent(
    val event: Event,
    val semester: Int?,
    val required: Boolean,
)