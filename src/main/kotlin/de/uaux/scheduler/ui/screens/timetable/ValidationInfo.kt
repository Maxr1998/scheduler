package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.ValidationState
import de.uaux.scheduler.ui.util.success

@Composable
fun ValidationInfo(
    validationState: ValidationState,
    onClick: () -> Unit,
) {
    val defaultIconColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    when (validationState) {
        ValidationState.VALIDATING -> Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = defaultIconColor,
            )
        }
        else -> IconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = when (validationState) {
                    ValidationState.UNKNOWN -> Icons.Outlined.HelpOutline
                    ValidationState.OUTDATED -> Icons.Outlined.Schedule
                    ValidationState.VALIDATING -> throw RuntimeException()
                    ValidationState.FOUND_PROBLEMS -> Icons.Outlined.ErrorOutline
                    ValidationState.OK -> Icons.Outlined.TaskAlt
                },
                tint = when (validationState) {
                    ValidationState.UNKNOWN,
                    ValidationState.OUTDATED -> defaultIconColor
                    ValidationState.VALIDATING -> throw RuntimeException()
                    ValidationState.FOUND_PROBLEMS -> MaterialTheme.colors.error
                    ValidationState.OK -> MaterialTheme.colors.success
                },
                contentDescription = null,
            )
        }
    }
}