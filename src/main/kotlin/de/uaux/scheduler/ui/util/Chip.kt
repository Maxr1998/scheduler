package de.uaux.scheduler.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    text: (@Composable () -> Unit)?,
    icon: ImageVector,
    onClickIcon: () -> Unit = {},
) {
    val iconOnly = text == null
    val chipColor = MaterialTheme.colors.onSurface.copy(alpha = CHIP_BACKGROUND_ALPHA).compositeOver(MaterialTheme.colors.surface)
    val iconColor = MaterialTheme.colors.onSurface.copy(alpha = CHIP_ICON_BACKGROUND_ALPHA)
    val chipShape = RoundedCornerShape(CHIP_RADIUS)
    Row(
        modifier = Modifier
            .height(CHIP_RADIUS * 2)
            .run { if (iconOnly) width(CHIP_RADIUS * 2) else this }
            .background(
                color = chipColor,
                shape = chipShape,
            )
            .clip(chipShape)
            .run { if (iconOnly) this else padding(start = 12.dp, end = 8.dp) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text != null) {
            ProvideTextStyle(MaterialTheme.typography.body2) {
                text()
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        IconButton(
            modifier = Modifier
                .size(if (iconOnly) CHIP_RADIUS * 2 else CHIP_BUTTON_SIZE)
                .clip(CircleShape)
                .run { if (iconOnly) this else background(iconColor) },
            onClick = onClickIcon,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (iconOnly) iconColor else chipColor,
            )
        }
    }
}

private val CHIP_RADIUS = 16.dp
private val CHIP_BUTTON_SIZE = 18.dp
private const val CHIP_BACKGROUND_ALPHA = 0.12f
private const val CHIP_ICON_BACKGROUND_ALPHA = 0.54f