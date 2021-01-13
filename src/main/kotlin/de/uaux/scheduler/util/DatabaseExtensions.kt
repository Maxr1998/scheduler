package de.uaux.scheduler.util

import de.uaux.scheduler.model.StandardQueries

/**
 * Checks if changes from last insert were applied successfully and returns the inserted id
 */
fun StandardQueries.checkAndGetId(): Long {
    val changes = changes().executeAsOneOrNull()
    return if (changes != null && changes > 0) {
        lastInsertRowId().executeAsOneOrNull() ?: -1
    } else -1
}