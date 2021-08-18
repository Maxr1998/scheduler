package de.uaux.scheduler.util

import de.uaux.scheduler.model.dto.ScheduledEvent
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.model.dto.UnscheduledEvent

typealias StudycourseEventMapper = (
    id: Long,
    name: String,
    type: Int,
    module: String,
    duration: Int,
    participants: Int?,
    studycourseSemester: Int,
    required: Boolean,
) -> StudycourseEvent

typealias ScheduledEventMapper = (
    id: Long,
    name: String,
    type: Int,
    module: String,
    duration: Int,
    participants: Int?,
    studycourseSemester: Int,
    required: Boolean,
    day: Int,
    startTime: Int,
    room: Long,
) -> ScheduledEvent

typealias UnscheduledEventMapper = (
    id: Long,
    name: String,
    type: Int,
    module: String,
    duration: Int,
    participants: Int?,
    studycourseSemester: Int,
    required: Boolean,
    count: Long,
) -> UnscheduledEvent