package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.ValidationState
import de.uaux.scheduler.ui.util.FixUnsupportedIntrinsicMeasurementsLayoutModifier
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.ui.util.success
import de.uaux.scheduler.util.formatTimeMinutesOfDay
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ValidationInfo(
    validationState: ValidationState,
    detailsState: MutableState<Boolean>,
    onClick: () -> Unit,
) {
    var showDetails by detailsState
    Row {
        StatusIconButton(
            validationState = validationState,
            onClick = onClick,
        )

        DropdownMenu(
            expanded = showDetails && validationState is ValidationState.FoundProblems,
            onDismissRequest = {
                showDetails = false
            },
            offset = DpOffset(x = (-160).dp, y = 0.dp),
        ) {
            Column(
                modifier = Modifier.size(900.dp, 400.dp),
            ) {
                ValidationInfoPanelContent(validationState = validationState)
            }
        }
    }
}

@Composable
private fun StatusIconButton(
    validationState: ValidationState,
    onClick: () -> Unit,
) {
    when (validationState) {
        ValidationState.Validating -> Box(
            modifier = Modifier.size(48.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colors.primary,
            )
        }
        else -> IconButton(
            onClick = onClick,
        ) {
            @OptIn(ExperimentalMaterialApi::class)
            BadgedBox(
                badge = {
                    if (validationState is ValidationState.FoundProblems) {
                        Badge {
                            Text(text = validationState.problemCount.toString())
                        }
                    }
                },
            ) {
                val defaultIconColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                Icon(
                    imageVector = when (validationState) {
                        ValidationState.Unknown -> Icons.Outlined.HelpOutline
                        ValidationState.Outdated -> Icons.Outlined.Schedule
                        ValidationState.Validating -> throw IllegalStateException("state cannot be Validating here")
                        is ValidationState.FoundProblems -> Icons.Outlined.ErrorOutline
                        ValidationState.Ok -> Icons.Outlined.TaskAlt
                    },
                    tint = when (validationState) {
                        ValidationState.Unknown,
                        ValidationState.Outdated -> defaultIconColor
                        ValidationState.Validating -> throw IllegalStateException("state cannot be Validating here")
                        is ValidationState.FoundProblems -> MaterialTheme.colors.error
                        ValidationState.Ok -> MaterialTheme.colors.success
                    },
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
private fun ValidationInfoPanelContent(validationState: ValidationState) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = l("validation_panel_header_conflicts"),
        style = MaterialTheme.typography.h5,
    )

    if (validationState !is ValidationState.FoundProblems) return

    LazyColumn(
        modifier = FixUnsupportedIntrinsicMeasurementsLayoutModifier,
    ) {
        for ((event, conflicts) in validationState.conflicts) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(event.event.name)
                        }
                        append(" (")
                        append(event.day.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                        append(", ")
                        append(formatTimeMinutesOfDay(event.startTime))
                        append('-')
                        append(formatTimeMinutesOfDay(event.endTime))
                        append(") ")
                        append(l("validation_panel_conflicts_with"))
                        append(':')
                    },
                )
            }

            items(conflicts) { conflict ->
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp),
                    text = buildAnnotatedString {
                        append("\u2022 ")
                        append(conflict.event.name)
                        append(" (")
                        append(formatTimeMinutesOfDay(conflict.startTime))
                        append('-')
                        append(formatTimeMinutesOfDay(conflict.endTime))
                        append(") ")

                        // TODO: show studycourse where the conflict comes from
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}