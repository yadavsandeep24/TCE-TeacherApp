package com.tce.teacherapp.ui.dashboard.messages

import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun MessageViewModel.setMessageList(messageList: List<Message>) {
    val update = getCurrentViewStateOrNew()
    update.messageList = messageList
    setViewState(update)
}