package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selected
import de.uaux.scheduler.ui.screens.timetable.ShowWeekendToggle
import de.uaux.scheduler.ui.screens.timetable.StudycourseAndSemesterSelectionDropdown
import de.uaux.scheduler.ui.screens.timetable.TimetableScreenContent
import de.uaux.scheduler.ui.screens.timetable.ValidationInfo
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
        ValidationInfo(
            validationState = timetableViewModel.validationState.value,
            onClick = {
                timetableViewModel.validate()
            },
        )

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