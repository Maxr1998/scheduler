package de.uaux.scheduler.ui.util

import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

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

@Composable
fun EditButton(onClick: () -> Unit) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        IconButton(
            onClick = onClick,
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
            )
        }
    }
}