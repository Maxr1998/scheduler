package de.uaux.scheduler.model.dto

import androidx.compose.runtime.Immutable
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Semester

@Immutable
data class Suggestion(
    val id: Long,
    val semester: Semester,
    val event: Event,
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