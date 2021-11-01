package de.uaux.scheduler.ui.model

import de.uaux.scheduler.model.dto.ScheduledEvent

sealed class ValidationState {
    object Unknown : ValidationState()
    object Outdated : ValidationState()
    object Validating : ValidationState()
    object Ok : ValidationState()

    class FoundProblems(val conflicts: List<Pair<ScheduledEvent, List<ScheduledEvent>>>) : ValidationState() {
        val problemCount: Int
            get() = conflicts.size
    }
}