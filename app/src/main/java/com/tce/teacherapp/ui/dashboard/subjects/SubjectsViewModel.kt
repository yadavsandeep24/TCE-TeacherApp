package com.tce.teacherapp.ui.dashboard.subjects

import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectStateEvent
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.*
import com.tce.teacherapp.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@UseExperimental(FlowPreview::class)
class SubjectsViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<SubjectViewState>() {

    override fun handleNewData(data: SubjectViewState) {
        data.gradeList?.let {
            setGradeListData(it)
        }
        data.selectedGradePosition?.let {
            setSelectedGradePosition(it)
        }
        data.subjectList?.let {
            setSubjectListData(it)
        }
        data.topicList?.let {
            setTopicListData(it)
        }
        data.chapterLearnData?.let {
            setChapterLearnData(it)
        }
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<SubjectViewState>> = when (stateEvent) {

            is SubjectStateEvent.GetDivisionEvent -> {
                mainRepository.getGrades(stateEvent = stateEvent)
            }

            is SubjectStateEvent.DivisionSelectionEvent -> {
                mainRepository.setSelectedGrade(
                    stateEvent.grade,
                    stateEvent.position,
                    stateEvent = stateEvent
                )
            }

            is SubjectStateEvent.GetSubjectEvent -> {
                mainRepository.getSubjects(stateEvent.query, stateEvent = stateEvent)
            }

            is SubjectStateEvent.GetTopicEvent -> {
                mainRepository.getTopicList(
                    stateEvent.query,
                    stateEvent.bookId,
                    stateEvent = stateEvent
                )
            }
            is SubjectStateEvent.GetChapterEvent -> {
                mainRepository.getChapterList(
                    stateEvent.query,
                    stateEvent.topicId,
                    stateEvent.bookId,
                    stateEvent = stateEvent
                )
            }
            is SubjectStateEvent.GetTopicResourceEvent ->{
                mainRepository.getTopicResourceList(
                    stateEvent.query,
                    stateEvent.topicId,
                    stateEvent = stateEvent
                )
            }
            else -> {
                flow {
                    emit(
                        DataState.error<SubjectViewState>(
                            response = Response(
                                message = INVALID_STATE_EVENT,
                                uiComponentType = UIComponentType.None,
                                messageType = MessageType.Error(),
                                serviceTypes = RequestTypes.GENERIC
                            ),
                            stateEvent = stateEvent
                        )
                    )
                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): SubjectViewState {
        return SubjectViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}