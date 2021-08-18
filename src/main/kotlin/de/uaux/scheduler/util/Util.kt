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

fun formatMinutesOfDay(minutes: Int) = "%d:%02dh".format(minutes / 60, minutes % 60)