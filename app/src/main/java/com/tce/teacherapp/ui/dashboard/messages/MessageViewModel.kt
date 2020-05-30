package com.tce.teacherapp.ui.dashboard.messages

import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.repository.MainRepository
import com.tce.teacherapp.ui.BaseViewModel
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
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
@FlowPreview
class MessageViewModel @Inject constructor(val mainRepository: MainRepository) :
    BaseViewModel<MessageViewState>() {

    override fun handleNewData(data: MessageViewState) {
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
        val job: Flow<DataState<MessageViewState>> = when (stateEvent) {

            is MessageStateEvent.GetMessageEvent -> {
                mainRepository.getMessage(stateEvent = stateEvent)
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