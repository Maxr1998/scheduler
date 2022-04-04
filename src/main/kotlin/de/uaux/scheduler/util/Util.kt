package de.uaux.scheduler.util

val IntRange.size get() = last - first

fun <T : Comparable<T>> List<T>.binaryInsertIndex(element: T): Int {
    val searchIndex = binarySearch(element)
    return if (searchIndex < 0) -(searchIndex + 1) else searchIndex
}

fun <T : Comparable<T>> MutableList<T>.binaryInsert(element: T) {
    add(binaryInsertIndex(element), element)
}

fun <T> List<T>.binaryInsertIndex(comparison: (T) -> Int): Int {
    val searchIndex = binarySearch(comparison = comparison)
    return if (searchIndex < 0) -(searchIndex + 1) else searchIndex
}

fun <T> MutableList<T>.binaryInsert(element: T, comparison: (T) -> Int) {
    add(binaryInsertIndex(comparison), element)
}

private const val MINUTES_PER_HOUR = 60

fun formatTimeMinutesOfDay(minutes: Int): String =
    "%02d:%02d".format(minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)

fun formatDurationMinutesOfDay(minutes: Int): String =
    "%d:%02dh".format(minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)