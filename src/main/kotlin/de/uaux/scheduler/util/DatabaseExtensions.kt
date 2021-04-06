package de.uaux.scheduler.util

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.db.use
import de.uaux.scheduler.model.StandardQueries

/**
 * Similar to [Query.executeAsList], but allows mapping the [RowType] to a different type [T] with [mapper].
 */
inline fun <RowType : Any, T> Query<RowType>.executeAsMappedList(mapper: (RowType) -> T): List<T> {
    val result = mutableListOf<T>()
    execute().use { cursor ->
        while (cursor.next()) result.add(mapper(mapper(cursor)))
    }
    return result
}

/**
 * Checks if changes from last insert/update were applied successfully
 */
fun StandardQueries.changedOne(): Boolean =
    changes().executeAsOneOrNull() == 1L

/**
 * Checks if changes from last insert were applied successfully and returns the inserted id
 */
fun StandardQueries.checkAndGetId(): Long =
    if (changedOne()) lastInsertRowId().executeAsOneOrNull() ?: -1 else -1