package de.uaux.scheduler.viewmodel

import de.uaux.scheduler.model.Semester
import de.uaux.scheduler.repository.EventRepository
import de.uaux.scheduler.repository.ScheduleRepository
import de.uaux.scheduler.repository.StudycourseRepository
import de.uaux.scheduler.repository.SuggestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class HomeViewModel(
    private val studycourseRepository: StudycourseRepository,
    private val eventRepository: EventRepository,
    private val scheduleRepository: ScheduleRepository,
    private val suggestionRepository: SuggestionRepository,
) {
    val studycourseCount: Flow<Long> get() = studycourseRepository.studycourseCountFlow
    val eventCount: Flow<Long> get() = eventRepository.eventCountFlow

    fun getSemester(): Semester = scheduleRepository.computeNextSemester()

    fun getSuggestionProgress(semester: Semester): Flow<Pair<Long, Long>?> {
        val unprocessedSuggestionCount = suggestionRepository.queryUnprocessedSuggestionCountBySemesterAsFlow(semester)
        val suggestionCount = suggestionRepository.querySuggestionCountBySemesterAsFlow(semester)

        return unprocessedSuggestionCount.combine(suggestionCount) { unprocessed, all ->
            if (all > 0) unprocessed to all else null
        }
    }
}