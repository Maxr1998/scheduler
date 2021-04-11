package de.uaux.scheduler.ui.screens.event_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.ui.model.StudycourseSelection
import de.uaux.scheduler.viewmodel.EventManagementViewModel
import org.koin.androidx.compose.get

@Composable
fun StudycoursesPane(
    studycourseSelection: StudycourseSelection,
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
            items(studycourses) { studycourse ->
                val selected = studycourseSelection.isSelected(studycourse)
                StudycourseListItem(
                    modifier = Modifier.selectable(selected) {
                        eventManagementViewModel.load(studycourse)
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
    val background = if (selected) Modifier.background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.12f)) else Modifier
    val textColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface

    val secondaryTextContent: @Composable (() -> Unit) = {
        Text(text = studycourse.revision.orEmpty())
    }
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(modifier)
            .then(background),
        text = {
            Text(
                text = studycourse.name,
                color = textColor,
            )
        },
        secondaryText = if (!studycourse.revision.isNullOrBlank()) secondaryTextContent else null,
        trailing = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = {
                        openDialog(studycourse)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}