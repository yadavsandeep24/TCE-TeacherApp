package com.tce.teacherapp.ui.dashboard.messages

import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.db.entity.MessageResource
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MessageViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<MessageViewState>() {

    override fun handleNewData(data: MessageViewState) {
        data.messageList?.let {
            setMessageList(it)
        }

        data.selectedMessage?.let {
            setSelectedMessage(it)
        }

        data.studentList?.let {
            setStudentList(it)
        }

        data.resourceList?.let {
            setResourceList(it)
        }

        data.selectedResourceList?.let {
            setSelectedResourceList(it)
        }

    }

    private fun setMessageList(messageList: List<Message>) {
        val update = getCurrentViewStateOrNew()
        update.messageList = messageList
        setViewState(update)
    }

    private fun setSelectedMessage(mesage: Message) {
        val update = getCurrentViewStateOrNew()
        update.selectedMessage = mesage
        setViewState(update)
    }

    private fun setStudentList(studentList: List<StudentListResponseItem>) {
        val update = getCurrentViewStateOrNew()
        update.studentList = studentList
        setViewState(update)
    }

    private fun setResourceList(resourceList: List<MessageResource>) {
        val update = getCurrentViewStateOrNew()
        update.resourceList = resourceList
        setViewState(update)
    }

    private fun setSelectedResourceList(resourceList: List<MessageResource>) {
        val update = getCurrentViewStateOrNew()
        update.selectedResourceList = resourceList
        setViewState(update)
    }


    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<MessageViewState>> = when (stateEvent) {

            is MessageStateEvent.GetMessageEvent -> {
                mainRepository.getMessage(stateEvent.query,stateEvent = stateEvent)
            }

            is MessageStateEvent.GetStudentEvent -> {
                mainRepository.getStudentList(stateEvent.classID,stateEvent.query,stateEvent = stateEvent)
            }

            is MessageStateEvent.GetMessageConversionEvent -> {
                mainRepository.getMessage(stateEvent.query,stateEvent = stateEvent)
            }

            is MessageStateEvent.GetResourceSelectionEvent -> {
                mainRepository.getSelectedResourceList(stateEvent.query,stateEvent.typeId, stateEvent = stateEvent)
            }


            is MessageStateEvent.GetResourceEvent -> {
                mainRepository.getResourceList(stateEvent.query,stateEvent = stateEvent)
            }

            else -> {
                flow{

                }
            }
        }
        launchJob(stateEvent, job)
    }

    override fun initNewViewState(): MessageViewState {
        return MessageViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}