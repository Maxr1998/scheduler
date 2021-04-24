package de.uaux.scheduler.ui.screens.management.event.suggestion

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.repository.ScheduleRepository
import org.koin.androidx.compose.get

@Composable
fun SemesterSelection(
    current: Semester?,
    onSelect: (Semester) -> Unit,
) {
    val scheduleRepository: ScheduleRepository = get()
    val semesters: List<Semester> by scheduleRepository.allSemestersFlow.collectAsState(emptyList())
    var semesterSelectionExpanded by remember { mutableStateOf(false) }
    Box {
        val borderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, SolidColor(borderColor), MaterialTheme.shapes.small)
                .clip(MaterialTheme.shapes.small)
                .clickable {
                    semesterSelectionExpanded = true
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = current?.toString().orEmpty(),
            )

            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = semesterSelectionExpanded,
            onDismissRequest = {
                semesterSelectionExpanded = false
            },
        ) {
            for (semester in semesters) {
                key(semester.code) {
                    DropdownMenuItem(
                        onClick = {
                            onSelect(semester)
                            semesterSelectionExpanded = false
                        },
                    ) {
                        Text(text = semester.toString())
                    }
                }
            }
        }
    }
}