package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selected
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.screens.timetable.StudycourseAndSemesterSelectionDropdown
import de.uaux.scheduler.ui.screens.timetable.TimetableScreenContent
import de.uaux.scheduler.ui.util.CenteredTextBox
import de.uaux.scheduler.ui.util.LoadingBox
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.TimetableViewModel
import org.koin.androidx.compose.get

@Composable
fun TimetableScreen() = Column {
    val timetableViewModel: TimetableViewModel = get()
    val selection = timetableViewModel.timetableSelection.value
    Toolbar(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        title = l("screen_timetable"),
    ) {
        if (selection is Selected) {
            ShowWeekendToggle(timetableViewModel.showWeekend)

            StudycourseAndSemesterSelectionDropdown(selection.value)
        }
    }
    DisableSelection {
        when (selection) {
            None -> CenteredTextBox(text = l("timetable_no_studycourses"))
            Loading -> LoadingBox()
            is Selected -> TimetableScreenContent(selection.value)
        }
    }
}

@Composable
private fun ShowWeekendToggle(weekendState: MutableState<ShowWeekend>) {
    var showWeekend by weekendState
    val isEnabled = { showWeekend != ShowWeekend.FORCE }
    val isChecked = { showWeekend != ShowWeekend.FALSE }
    Row(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(enabled = isEnabled()) {
                showWeekend = if (!isChecked()) ShowWeekend.TRUE else ShowWeekend.FALSE
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(end = 12.dp),
            text = l("switch_label_show_weekend"),
            style = MaterialTheme.typography.body2,
        )

        Switch(
            enabled = isEnabled(),
            checked = isChecked(),
            onCheckedChange = { checked ->
                showWeekend = if (checked) ShowWeekend.TRUE else ShowWeekend.FALSE
            }
        )
    }
}