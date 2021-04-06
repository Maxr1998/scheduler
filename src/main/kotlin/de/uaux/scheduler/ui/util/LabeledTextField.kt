package de.uaux.scheduler.ui.util

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun LabeledTextField(
    text: MutableState<TextFieldValue>,
    label: String,
    placeholder: String,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text.value,
        singleLine = true,
        label = {
            Text(text = label)
        },
        placeholder = {
            Text(text = placeholder)
        },
        onValueChange = { value ->
            text.value = value
        },
    )
}