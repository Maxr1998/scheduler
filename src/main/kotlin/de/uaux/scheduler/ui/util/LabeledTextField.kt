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
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().then(modifier),
            value = text.value,
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

@Composable
fun calculateNumberInputError(value: String, allowedRange: LongRange, outOfRangeError: String): Pair<Long?, String?> {
    val number = value.trim().toLongOrNull()
    return number to when {
        value.isBlank() -> null
        number == null -> l("input_error_only_numbers")
        number !in allowedRange -> l(outOfRangeError)
        else -> null
    }
}