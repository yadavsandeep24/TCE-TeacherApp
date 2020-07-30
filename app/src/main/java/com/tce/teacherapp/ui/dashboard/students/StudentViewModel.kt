package com.tce.teacherapp.ui.dashboard.students

import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.db.entity.MessageResource
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
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
        data.messageList?.let {
            setMessageList(it)
        }


    }

    private fun setMessageList(messageList: List<Message>) {
        val update = getCurrentViewStateOrNew()
        update.messageList = messageList
        setViewState(update)
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<StudentViewState>> = when (stateEvent) {

            is StudentStateEvent.GetStudentEvent -> {
                mainRepository.getStudentData(stateEvent.query,stateEvent = stateEvent)
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