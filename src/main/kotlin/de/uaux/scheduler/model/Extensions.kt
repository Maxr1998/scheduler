package de.uaux.scheduler.model

val Timeslot.duration
    get() = end_time - start_time