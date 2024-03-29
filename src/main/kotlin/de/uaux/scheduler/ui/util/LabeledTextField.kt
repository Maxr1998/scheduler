package de.uaux.scheduler.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun LabeledTextField(
    modifier: Modifier = Modifier,
    text: MutableState<TextFieldValue>,
    label: String,
    placeholder: String,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
) {
    Column(
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().run { if (!singleLine) weight(1f) else this },
            value = text.value,
            readOnly = readOnly,
            singleLine = singleLine,
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeholder)
            },
            onValueChange = { value ->
                text.value = value
            },
            isError = errorMessage != null,
            trailingIcon = {
                if (errorMessage != null) {
                    Icon(
                        imageVector = Icons.Outlined.Error,
                        contentDescription = "error",
                    )
                }
            },
        )
        Box(
            modifier = Modifier.height(20.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.error,
                )
            }
        }
    }
}

@Immutable
data class NumberInputResult(
    val value: Long?,
    val error: String?,
)

fun parseNumberInput(value: String, allowedRange: LongRange, outOfRangeError: String): NumberInputResult {
    val parsed = value.trim().toLongOrNull()
    return NumberInputResult(
        value = parsed,
        error = when {
            value.isBlank() -> null
            parsed == null -> "input_error_only_numbers"
            parsed !in allowedRange -> outOfRangeError
            else -> null
        },
    )
}