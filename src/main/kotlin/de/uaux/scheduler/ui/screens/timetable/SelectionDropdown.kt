package de.uaux.scheduler.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.model.TimetableFilter
import de.uaux.scheduler.ui.util.FixUnsupportedIntrinsicMeasurementsLayoutModifier
import de.uaux.scheduler.ui.util.VerticalDivider
import de.uaux.scheduler.ui.util.disabled
import de.uaux.scheduler.viewmodel.TimetableViewModel
import org.koin.androidx.compose.get

@Composable
fun StudycourseAndSemesterSelectionDropdown(filter: TimetableFilter) {
    var expanded by remember { mutableStateOf(false) }
    val shape = MaterialTheme.shapes.medium
    Row(
        modifier = Modifier
            .height(42.dp)
            .clip(shape)
            .clickable { expanded = true }
            .border(1.dp, MaterialTheme.colors.disabled, shape)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowDropDown,
            contentDescription = "",
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = "${filter.studycourse.name} | ${filter.semester}",
            style = MaterialTheme.typography.button,
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
        ) {
            StudycourseAndSemesterSelectionDropdownMenuContent(filter)
        }
    }
}

@Composable
private fun StudycourseAndSemesterSelectionDropdownMenuContent(filter: TimetableFilter) {
    Row(
        modifier = Modifier.size(600.dp, 300.dp),
    ) {
        val timetableViewModel: TimetableViewModel = get()
        val studycourses by timetableViewModel.studycoursesFlow.collectAsState(emptyList())
        val semesters by timetableViewModel.semestersFlow.collectAsState(emptyList())
        var selectedStudycourse by remember { mutableStateOf(filter.studycourse) }
        var selectedSemester by remember { mutableStateOf(filter.semester) }

        val selectedBackground = Modifier.background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.12f))
        Column(
            modifier = Modifier.weight(5f)
        ) {
            LazyColumn(
                modifier = FixUnsupportedIntrinsicMeasurementsLayoutModifier,
            ) {
                items(studycourses) { studycourse ->
                    DropdownMenuItem(
                        modifier = if (studycourse == selectedStudycourse) selectedBackground else Modifier,
                        onClick = {
                            if (selectedStudycourse != studycourse) {
                                selectedStudycourse = studycourse
                                timetableViewModel.loadContent(selectedSemester, studycourse)
                            }
                        },
                    ) {
                        Text(text = studycourse.name)
                    }
                }
            }
        }
        VerticalDivider()
        Column(
            modifier = Modifier.weight(2f),
        ) {
            LazyColumn(
                modifier = FixUnsupportedIntrinsicMeasurementsLayoutModifier,
            ) {
                items(semesters) { semester ->
                    DropdownMenuItem(
                        modifier = if (semester == selectedSemester) selectedBackground else Modifier,
                        onClick = {
                            if (selectedSemester != semester) {
                                selectedSemester = semester
                                timetableViewModel.loadContent(semester, selectedStudycourse)
                            }
                        },
                    ) {
                        Text(text = semester.toString())
                    }
                }
            }
        }
    }
}