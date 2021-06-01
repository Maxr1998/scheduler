package de.uaux.scheduler.ui.util

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun <T : Any> SearchableSelectionDropdown(
    label: String,
    placeholder: String,
    onSearch: (String) -> Unit,
    searchResults: List<T>,
    onSelect: (T) -> Unit,
    selected: T?,
    itemLabel: (T) -> String = Any::toString,
    itemContent: @Composable (T) -> Unit,
) {
    Column(
        modifier = Modifier.border(1.dp, SolidColor(MaterialTheme.colors.disabled), MaterialTheme.shapes.medium)
    ) {
        val searchText = remember { mutableStateOf(TextFieldValue(selected?.let(itemLabel).orEmpty())) }
        var showResults by remember { mutableStateOf(false) }

        // Set text on selection
        LaunchedEffect(selected) {
            if (selected != null) {
                searchText.value = TextFieldValue(itemLabel(selected))
            }
        }

        TextField(
            modifier = Modifier
                .onFocusChanged { state ->
                    showResults = state.isFocused
                }
                .fillMaxWidth(),
            label = {
                Text(text = label)
            },
            placeholder = {
                Text(text = placeholder)
            },
            value = searchText.value,
            onValueChange = { value ->
                if (value.text != searchText.value.text) {
                    onSearch(value.text)
                }
                searchText.value = value
            },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
            ),
            trailingIcon = {
                if (selected != null) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                    )
                }
            },
        )

        if (showResults && selected == null) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(0.dp, 260.dp),
            ) {
                items(searchResults) { item ->
                    DropdownMenuItem(onClick = {
                        onSelect(item)
                    }) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}