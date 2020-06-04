package com.tce.teacherapp.ui.dashboard.messages.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.db.entity.Student
import kotlinx.android.parcel.Parcelize

const val MESSAGE_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class MessageViewState(
    var messageList: List<Message>? = null,
    var selectedMessage: Message? = null,
    var studentList : List<Student>? = null

) : Parcelable
