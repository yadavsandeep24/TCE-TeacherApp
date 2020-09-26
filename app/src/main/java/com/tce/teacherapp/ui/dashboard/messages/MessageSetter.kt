package com.tce.teacherapp.ui.dashboard.messages

import com.tce.teacherapp.api.response.MessageListResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun MessageViewModel.setMessageList(messageResponse: MessageListResponse) {
    val update = getCurrentViewStateOrNew()
    update.messageResponse = messageResponse
    setViewState(update)
}