package de.uaux.scheduler.util

fun formatMinutesOfDay(minutes: Int) = "%d:%02d".format(minutes / 60, minutes % 60)