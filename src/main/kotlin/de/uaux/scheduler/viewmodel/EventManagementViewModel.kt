package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import de.uaux.scheduler.model.Event
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.model.dto.StudycourseEvent
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.model.Loading
import de.uaux.scheduler.ui.model.None
import de.uaux.scheduler.ui.model.Selection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EventManagementViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val eventRepository: EventRepository,
) {
    private val coroutineScope = MainScope()

    val eventsFlow: Flow<List<Event>> get() = eventRepository.allEventsFlow
    val studycoursesFlow: Flow<List<Studycourse>> get() = studycourseRepository.allStudycoursesFlow

    private val _studycourseSelection: MutableState<Selection<Studycourse>> = mutableStateOf(Loading)
    val studycourseSelection: State<Selection<Studycourse>> = _studycourseSelection

    init {
        coroutineScope.launch {
            studycoursesFlow.collect { studycourses ->
                if (studycourses.isNotEmpty()) {
                    select(studycourses[0])
                    cancel()
                } else {
                    _studycourseSelection.value = None
                }
            }
        }
    }

    fun select(studycourse: Studycourse) {
        _studycourseSelection.value = Selection(studycourse)
    }

    fun getStudycourseEvents(studycourse: Studycourse): Flow<List<StudycourseEvent>> =
        eventRepository.queryAllInStudycourseAsFlow(studycourse)
}