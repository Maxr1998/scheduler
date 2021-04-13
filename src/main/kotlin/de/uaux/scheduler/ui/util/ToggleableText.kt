package de.uaux.scheduler.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToggleableText(
    modifier: Modifier,
    state: MutableState<Boolean>,
    text: String,
    checkedText: String? = null,
) {
    Row(
        modifier = modifier
            .clickable {
                state.value = !state.value
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = if (state.value && checkedText != null) checkedText else text,
        )

        Switch(
            checked = state.value,
            onCheckedChange = { checked ->
                state.value = checked
            }
        )
    }
}