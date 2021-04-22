package de.uaux.scheduler.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class Selection<out T> {
    fun <T> isActive(some: T): Boolean = this is Selected && some == value

    fun orNull(): T? = when (this) {
        is Selected -> value
        else -> null
    }

    companion object {
        operator fun <T> invoke(some: T): Selection<T> = Selected(some)
    }
}

@Immutable
object None : Selection<Nothing>()

@Immutable
object Loading : Selection<Nothing>()

@Immutable
data class Selected<out T>(val value: T) : Selection<T>()