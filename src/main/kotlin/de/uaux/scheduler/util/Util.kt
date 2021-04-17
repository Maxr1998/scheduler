package de.uaux.scheduler.util

fun <T : Comparable<T>> List<T>.binaryInsertIndex(element: T): Int {
    val searchIndex = binarySearch(element)
    return if (searchIndex < 0) -(searchIndex + 1) else searchIndex
}

fun <T : Comparable<T>> MutableList<T>.binaryInsert(element: T) {
    add(binaryInsertIndex(element), element)
}

fun formatMinutesOfDay(minutes: Int) = "%d:%02d".format(minutes / 60, minutes % 60)