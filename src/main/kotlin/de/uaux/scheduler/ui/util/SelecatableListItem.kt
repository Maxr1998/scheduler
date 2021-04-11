package de.uaux.scheduler.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@ExperimentalMaterialApi
@Composable
fun SelectableListItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: @Composable () -> Unit,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val background = if (selected) Modifier.background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.12f)) else Modifier
    val textColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(modifier)
            .then(background),
        text = {
            CompositionLocalProvider(LocalContentColor provides textColor) {
                text()
            }
        },
        secondaryText = secondaryText,
        trailing = trailing,
    )
}