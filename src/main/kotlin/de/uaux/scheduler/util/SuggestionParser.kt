package de.uaux.scheduler.util

import de.uaux.scheduler.model.EventSuggestion

class SuggestionParser {

    fun parseConstraint(type: Int, value: ByteArray): EventSuggestion.Constraint {
        // TODO: parse suggestions
        return EventSuggestion.Constraint.Unparsed(type, value)
    }
}