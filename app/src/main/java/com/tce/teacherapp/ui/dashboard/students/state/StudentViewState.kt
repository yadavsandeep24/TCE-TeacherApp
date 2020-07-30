package com.tce.teacherapp.ui.dashboard.students.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.Message
import com.tce.teacherapp.db.entity.MessageResource
import com.tce.teacherapp.db.entity.Student
import kotlinx.android.parcel.Parcelize

const val STUDENT_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class StudentViewState(
    var messageList: List<Message>? = null

) : Parcelable
