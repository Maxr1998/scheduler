package de.uaux.scheduler.util

import de.uaux.scheduler.model.dto.Suggestion

class SuggestionParser {

    fun parseConstraint(type: Int, value: ByteArray): Suggestion.Constraint {
        // TODO: parse suggestions
        return Suggestion.Constraint.Unparsed(type, value)
    }
}