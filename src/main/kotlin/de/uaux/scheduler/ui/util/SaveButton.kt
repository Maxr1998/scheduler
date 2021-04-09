package de.uaux.scheduler.ui.util

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SaveButton(
    enabled: Boolean = true,
    onSave: () -> Unit,
) {
    TextButton(
        enabled = enabled,
        onClick = onSave,
    ) {
        Text(text = l("button_text_save"))
    }
}