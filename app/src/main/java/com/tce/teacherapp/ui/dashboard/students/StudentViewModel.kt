package com.tce.teacherapp.ui.dashboard.students

import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class StudentViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<StudentViewState>() {

    override fun handleNewData(data: StudentViewState) {

        data.studentListResponse?.let {
            val update = getCurrentViewStateOrNew()
            update.studentListResponse = it
            setViewState(update)
        }
        data.studentattendancedata?.let {
            val update = getCurrentViewStateOrNew()
            update.studentattendancedata = it
            setViewState(update)
        }
        data.feedbackMaster?.let {
            val update = getCurrentViewStateOrNew()
            update.feedbackMaster = it
            setViewState(update)
        }
        data.studentportfolioresponse?.let {
            val update = getCurrentViewStateOrNew()
            update.studentportfolioresponse = it
            setViewState(update)
        }
        data.studentgallerydata?.let {
            val update = getCurrentViewStateOrNew()
            update.studentgallerydata = it
            setViewState(update)
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<StudentViewState>> = when (stateEvent) {

            is StudentStateEvent.GetStudentEvent -> {
                mainRepository.getStudentData(stateEvent.classID,stateEvent.query,stateEvent = stateEvent)
            }
            is StudentStateEvent.GetAttendanceData -> {
                mainRepository.getStudentAttendanceData(stateEvent = stateEvent)
            }
            is StudentStateEvent.GetFeedbackMaster ->{
                mainRepository.getFeedBackMasterData(stateEvent = stateEvent)
            }
            is StudentStateEvent.GetGalleryData ->{
                mainRepository.getGalleryData(stateEvent.type,stateEvent =  stateEvent)
            }
            is StudentStateEvent.GetStudentPortfolio ->{
                mainRepository.getStudentPortfolio(stateEvent.type,stateEvent =  stateEvent)
            }
            else -> {
                flow{

                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): StudentViewState {
        return StudentViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}