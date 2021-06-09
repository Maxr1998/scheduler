package de.uaux.scheduler.ui.screens.management.studycourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.ui.model.Selection
import de.uaux.scheduler.ui.util.EditButton
import de.uaux.scheduler.ui.util.SelectableListItem
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudycoursesPane(
    selection: Selection<Studycourse>,
    openDialog: (Studycourse?) -> Unit,
) {
    val eventManagementViewModel: EventManagementViewModel = get()
    val studycourses by eventManagementViewModel.studycoursesFlow.collectAsState(emptyList())
    Column(
        modifier = Modifier.width(280.dp).fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 20.dp).padding(top = 12.dp),
                    text = l("studycourse_panel_header"),
                    color = MaterialTheme.colors.secondary,
                    style = MaterialTheme.typography.caption,
                )
            }
            items(studycourses, key = { studycourse -> selection to studycourse }) { studycourse ->
                val selected = selection.isActive(studycourse)
                StudycourseListItem(
                    modifier = Modifier.selectable(selected) {
                        eventManagementViewModel.select(studycourse)
                    },
                    studycourse = studycourse,
                    selected = selected,
                    openDialog = openDialog,
                )
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            IconButton(
                onClick = { openDialog(null) },
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StudycourseListItem(
    modifier: Modifier = Modifier,
    studycourse: Studycourse,
    selected: Boolean,
    openDialog: (Studycourse?) -> Unit,
) {
    val secondaryTextContent: @Composable (() -> Unit) = {
        Text(text = studycourse.revision.orEmpty())
    }
    SelectableListItem(
        modifier = modifier,
        selected = selected,
        text = {
            Text(text = studycourse.name)
        },
        secondaryText = if (!studycourse.revision.isNullOrBlank()) secondaryTextContent else null,
        trailing = {
            EditButton(
                onClick = { openDialog(studycourse) },
            )
        },
    )
}