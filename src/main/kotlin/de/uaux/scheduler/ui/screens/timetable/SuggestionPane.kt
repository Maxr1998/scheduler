package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import de.uaux.scheduler.model.dto.Suggestion
import de.uaux.scheduler.ui.util.DraggableCard
import de.uaux.scheduler.ui.util.ZIndex
import de.uaux.scheduler.viewmodel.TimetableViewModel
import org.koin.androidx.compose.get

@Composable
fun SuggestionsPane(modifier: Modifier = Modifier) {
    val timetableViewModel: TimetableViewModel = get()
    Surface(
        modifier = Modifier.zIndex(ZIndex.SIDE_PANEL),
        elevation = 1.dp,
    ) {
        LazyColumn(
            modifier = modifier,
        ) {
            val suggestions = timetableViewModel.suggestions
            items(suggestions) { suggestion ->
                SuggestionCard(suggestion = suggestion)
            }
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: Suggestion) {
    DraggableCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = suggestion.event.name,
                fontSize = 14.sp,
                style = MaterialTheme.typography.h6,
            )
        }
    }
}