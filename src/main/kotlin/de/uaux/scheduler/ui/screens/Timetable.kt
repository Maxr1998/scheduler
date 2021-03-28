package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import de.uaux.scheduler.ui.model.ShowWeekend
import de.uaux.scheduler.ui.model.TimetableSelection
import de.uaux.scheduler.ui.screens.timetable.StudycourseAndSemesterSelectionDropdown
import de.uaux.scheduler.ui.screens.timetable.SuggestionsPane
import de.uaux.scheduler.ui.screens.timetable.TimetablePane
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.ZIndex
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
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp),
            text = l("switch_label_show_weekend"),
            style = MaterialTheme.typography.body2,
        )

        var showWeekend by timetableViewModel.showWeekend
        Switch(
            modifier = Modifier.padding(end = 16.dp),
            enabled = showWeekend != ShowWeekend.FORCE,
            checked = showWeekend != ShowWeekend.FALSE,
            onCheckedChange = { checked ->
                showWeekend = if (checked) ShowWeekend.TRUE else ShowWeekend.FALSE
            }
        )

        if (selection is TimetableSelection.Loaded) {
            StudycourseAndSemesterSelectionDropdown(selection)
        }
    }
    DisableSelection {
        when (selection) {
            TimetableSelection.None -> Unit // TODO
            TimetableSelection.Loading -> Unit // TODO
            is TimetableSelection.Loaded -> TimetableScreenContent()
        }
    }
}

@Composable
private fun TimetableScreenContent() {
    Row {
        TimetablePane(
            modifier = Modifier.weight(1f, true).fillMaxHeight(),
        )
        VerticalDivider(modifier = Modifier.zIndex(ZIndex.DIVIDER))
        SuggestionsPane(
            modifier = Modifier.width(240.dp).fillMaxHeight(),
        )
    }
}