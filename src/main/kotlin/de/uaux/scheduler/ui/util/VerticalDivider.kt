package de.uaux.scheduler.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A divider is a thin line that groups content in lists and layouts
 *
 * @param color color of the divider line
 * @param thickness thickness of the divider line, 1 dp is used by default
 * @param topIndent start offset of this line, no offset by default
 */
@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = DividerAlpha),
    thickness: Dp = 1.dp,
    topIndent: Dp = 0.dp
) {
    val indentMod = if (topIndent.value != 0f) {
        Modifier.padding(top = topIndent)
    } else {
        Modifier
    }
    Box(
        modifier = modifier.then(indentMod)
            .fillMaxHeight()
            .preferredWidth(thickness)
            .background(color = color),
    )
}

private const val DividerAlpha = 0.12f