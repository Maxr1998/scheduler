package de.uaux.scheduler.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun PopupDialog(
    title: String? = null,
    onDismissRequest: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Popup(
        alignment = Alignment.Center,
        isFocusable = true,
        onDismissRequest = onDismissRequest,
    ) {
        val dialogFocusRequester = remember { FocusRequester() }
        Surface(
            modifier = Modifier
                .width(560.dp)
                .focusRequester(dialogFocusRequester)
                .focusModifier()
                .onKeyEvent { keyEvent ->
                    val isEscape = keyEvent.key == Key.Escape
                    if (isEscape) {
                        onDismissRequest?.invoke()
                    }
                    isEscape
                },
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
        ) {
            Column {
                if (title != null) {
                    Box(
                        modifier = Modifier.padding(horizontal = 24.dp).height(64.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h6,
                        )
                    }
                }

                Box(
                    modifier = Modifier.padding(horizontal = 24.dp),
                ) {
                    content()
                }

                Row(
                    modifier = Modifier.height(52.dp).fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions()
                }
            }
        }

        SideEffect {
            dialogFocusRequester.requestFocus()
        }
    }
}