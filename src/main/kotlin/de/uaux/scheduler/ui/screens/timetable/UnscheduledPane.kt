package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.ui.util.DraggableCard
import de.uaux.scheduler.viewmodel.TimetableViewModel
import org.koin.androidx.compose.get

@Composable
fun UnscheduledPane(modifier: Modifier = Modifier) {
    val timetableViewModel: TimetableViewModel = get()
    LazyColumn(
        modifier = modifier,
    ) {
        items(timetableViewModel.unscheduledEvents) { unscheduled ->
            EventCard(event = unscheduled)
        }
    }
}

@Composable
private fun EventCard(event: Event) {
    DraggableCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = event.name,
                fontSize = 14.sp,
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}