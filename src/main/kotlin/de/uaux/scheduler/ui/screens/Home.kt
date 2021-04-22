package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.controller.NavigationController
import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.ui.util.l
import de.uaux.scheduler.viewmodel.HomeViewModel
import org.koin.androidx.compose.get

@Composable
fun HomeScreen() = Column {
    Toolbar(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        title = l("screen_home"),
        actions = {},
    )

    DisableSelection {
        HomeScreenContent()
    }
}

@Composable
private fun HomeScreenContent() {
    val homeViewModel: HomeViewModel = get()
    val currentSemester = remember { homeViewModel.getSemester() }
    val studycourseCount by homeViewModel.studycourseCount.collectAsState(null)
    val eventCount by homeViewModel.eventCount.collectAsState(null)
    val suggestionsProgress by homeViewModel.getSuggestionProgress(currentSemester).collectAsState(null)

    val navigationController: NavigationController = get()
    var currentScreen by navigationController.currentScreen

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        HeaderText(currentSemester)

        Row {
            studycourseCount?.let { count ->
                DetailsItem(
                    modifier = Modifier.clickable {
                        currentScreen = NavigationController.Screen.Studycourses
                    },
                    label = l("home_screen_label_studycourses"),
                    count = count,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            eventCount?.let { count ->
                DetailsItem(
                    modifier = Modifier.clickable {
                        currentScreen = NavigationController.Screen.Events
                    },
                    label = l("home_screen_label_events"),
                    count = count,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            suggestionsProgress?.let { progress ->
                Progress(
                    modifier = Modifier.clickable {
                        currentScreen = NavigationController.Screen.Timetable
                    },
                    progress = progress,
                )
            }
        }
    }
}

@Composable
private fun HeaderText(semester: Semester) {
    Text(
        modifier = Modifier.padding(bottom = 24.dp),
        text = l("home_screen_header").format(semester),
        style = MaterialTheme.typography.h5,
    )
}

@Composable
private fun DetailsItem(
    modifier: Modifier = Modifier,
    label: String,
    count: Long,
) {
    Box(
        modifier = Modifier
            .size(144.dp)
            .background(MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            .then(modifier)
            .padding(horizontal = 8.dp)
            .padding(top = 36.dp, bottom = 28.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = count.toString(),
                style = MaterialTheme.typography.h3,
                maxLines = 1,
            )

            Text(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = label,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Progress(
    modifier: Modifier = Modifier,
    progress: Pair<Long, Long>,
) {
    Box(
        modifier = Modifier
            .size(288.dp, 144.dp)
            .background(MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            .then(modifier)
            .padding(horizontal = 16.dp)
            .padding(top = 42.dp, bottom = 20.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onPrimary) {
            Text(
                modifier = Modifier.align(Alignment.TopStart),
                text = l("home_screen_progress_report").format(progress.second, progress.first),
                style = MaterialTheme.typography.h6,
            )

            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart),
                progress = if (progress.second > 0) progress.first.toFloat() / progress.second else 1f,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }
}