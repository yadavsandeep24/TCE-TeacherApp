package com.tce.teacherapp.ui.dashboard.messages.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.MessageListResponse
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.db.entity.MessageResource
import com.tce.teacherapp.db.entity.Resource
import kotlinx.android.parcel.Parcelize

const val MESSAGE_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState"

@Parcelize
data class MessageViewState(
    var messageResponse: MessageListResponse? = null,
    var selectedMessage: Message? = null,
    var studentList : List<StudentListResponseItem>? = null,
    var resourceList : List<MessageResource>? = null,
    var selectedResourceList : List<Resource>? = null,
    var resourceTypeList: List<Resource>? = null

) : Parcelable
