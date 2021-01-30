package de.uaux.scheduler.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.uaux.scheduler.ui.util.Toolbar
import de.uaux.scheduler.util.AmbientLocalization

@Composable
fun TimetableScreen() = Column {
    Toolbar(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        title = AmbientLocalization.current["screen_timetable"],
    ) {}
}