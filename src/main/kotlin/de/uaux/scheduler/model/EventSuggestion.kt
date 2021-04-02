package de.uaux.scheduler.model

import androidx.compose.runtime.Immutable

@Immutable
data class EventSuggestion(
    val id: Long,
    val event: Event,
    val duration: Int,
    /**
     * A free-form text to contain the original request from the lecturer
     */
    val text: String,
    val constraints: List<Constraint>,
) {
    sealed class Constraint {
        @Immutable
        data class Unparsed(
            val type: Int,
            val value: ByteArray,
        ) : Constraint() {
            override fun equals(other: Any?): Boolean = when {
                this === other -> true
                javaClass != other?.javaClass -> false
                type != (other as Unparsed).type -> false
                !value.contentEquals(other.value) -> false
                else -> true
            }

            override fun hashCode(): Int = 31 * type + value.contentHashCode()
        }
    }
}