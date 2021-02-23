package de.uaux.scheduler.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Toolbar(
    modifier: Modifier,
    title: String,
    actions: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        elevation = 4.dp,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            DisableSelection {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.CenterStart),
                    style = MaterialTheme.typography.subtitle1,
                )
            }
            actions()
        }
    }
}