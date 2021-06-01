package de.uaux.scheduler.ui.util

import androidx.compose.desktop.LocalAppWindow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeysSet
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun PopupDialog(
    title: String? = null,
    onDismissRequest: (() -> Unit),
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val keyboard = LocalAppWindow.current.keyboard
    DisposableEffect(key1 = keyboard) {
        keyboard.setShortcut(Key.Escape) { onDismissRequest() }
        onDispose {
            keyboard.removeShortcut(KeysSet(Key.Escape))
        }
    }

    Popup(
        focusable = true,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = DrawerDefaults.ScrimOpacity)),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.width(560.dp),
                shape = MaterialTheme.shapes.medium,
                elevation = 24.dp,
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
                        modifier = Modifier.weight(1f, fill = false).padding(horizontal = 24.dp),
                    ) {
                        content()
                    }

                    if (actions != null) {
                        Row(
                            modifier = Modifier.height(52.dp).fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            actions()
                        }
                    }
                }
            }
        }
    }
}