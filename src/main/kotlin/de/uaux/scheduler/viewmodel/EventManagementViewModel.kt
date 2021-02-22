package de.uaux.scheduler.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import de.uaux.scheduler.model.Studycourse
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.ui.model.StudycourseSelection
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EventManagementViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val eventRepository: EventRepository,
) {
    private val coroutineScope = MainScope()

    val studycourses: SnapshotStateList<Studycourse> = mutableStateListOf()
    private val _studycourseSelection: MutableState<StudycourseSelection> = mutableStateOf(StudycourseSelection.None)
    val studycourseSelection: State<StudycourseSelection> = _studycourseSelection

    init {
        coroutineScope.launch {
            studycourseRepository.allStudycoursesFlow.collect { courses ->
                studycourses.clear()
                studycourses.addAll(courses)
            }
        }
    }

    fun load(studycourse: Studycourse) {
        val events = eventRepository.queryAllInStudycourseAsFlow(studycourse)
        _studycourseSelection.value = StudycourseSelection.Selected(studycourse, events)
    }
}