package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.model.StudycourseSelection
import kotlinx.coroutines.flow.Flow

class EventManagementViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val eventRepository: EventRepository,
) {
    private val defaultSelection = StudycourseSelection.None(eventRepository.allEventsFlow)

    val studycoursesFlow: Flow<List<Studycourse>> get() = studycourseRepository.allStudycoursesFlow
    private val _studycourseSelection: MutableState<StudycourseSelection> = mutableStateOf(defaultSelection)
    val studycourseSelection: State<StudycourseSelection> = _studycourseSelection

    fun showAll() {
        _studycourseSelection.value = defaultSelection
    }

    fun load(studycourse: Studycourse) {
        val events = eventRepository.queryAllInStudycourseAsFlow(studycourse)
        _studycourseSelection.value = StudycourseSelection.Selected(studycourse, events)
    }
}