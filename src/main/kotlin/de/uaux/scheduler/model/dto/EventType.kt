package de.uaux.scheduler.model.dto

import de.uaux.scheduler.model.Event

/**
 * Type of event as Enum.
 * IMPORTANT: Don't change or reorder types, since [ordinal] is persisted to the database!
 *
 * @see Event.type
 */
enum class EventType {
    UNDEFINED,
    LECTURE,
    TUTORIAL,
    SEMINAR,
}