package de.uaux.scheduler.ui.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex

private const val POINTER_KEY_DRAGGABLE = "draggable-card"

@Composable
fun DraggableCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    onClick: () -> Unit = { },
    onDragStart: () -> Unit = { },
    onDragUpdate: (offset: Offset) -> Unit = { },
    onDrop: (offset: Offset, success: Boolean) -> Unit = { _, _ -> },
    content: @Composable () -> Unit,
) {
    // The current offsets as states
    var offset: Offset by remember { mutableStateOf(Offset.Zero) }

    Card(
        modifier = Modifier
            .zIndex(if (offset != Offset.Zero) ZIndex.DRAGGABLE else 0f)
            .then(modifier)
            .offset { offset.round() }
            .padding(4.dp)
            .clickable(onClick = onClick)
            .pointerInput(POINTER_KEY_DRAGGABLE) {
                detectDragGestures(
                    onDragStart = {
                        offset = Offset.Zero
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        offset += dragAmount
                        onDragUpdate(offset)
                    },
                    onDragEnd = {
                        onDrop(offset, true)
                        offset = Offset.Zero
                    },
                    onDragCancel = {
                        onDrop(offset, false)
                        offset = Offset.Zero
                    },
                )
            },
        backgroundColor = backgroundColor,
        elevation = if (offset != Offset.Zero) 6.dp else 2.dp,
    ) {
        content()
    }
}