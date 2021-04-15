package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.model.StudycourseSelection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EventManagementViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val eventRepository: EventRepository,
) {
    private val coroutineScope = MainScope()

    val eventsFlow: Flow<List<Event>> get() = eventRepository.allEventsFlow
    val studycoursesFlow: Flow<List<Studycourse>> get() = studycourseRepository.allStudycoursesFlow

    private val _studycourseSelection: MutableState<StudycourseSelection> = mutableStateOf(StudycourseSelection.None)
    val studycourseSelection: State<StudycourseSelection> = _studycourseSelection

    init {
        coroutineScope.launch {
            studycoursesFlow.collect { studycourses ->
                if (studycourses.isNotEmpty()) {
                    load(studycourses[0])
                    cancel()
                }
            }
        }
    }

    fun load(studycourse: Studycourse) {
        val events = eventRepository.queryAllInStudycourseAsFlow(studycourse)
        _studycourseSelection.value = StudycourseSelection.Selected(studycourse, events)
    }
}