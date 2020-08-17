package com.tce.teacherapp.ui.dashboard.students.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.*
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

const val STUDENT_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.dashboard.student.state.StudentViewState"

@Parcelize
data class StudentViewState (
    var studentListResponse: @RawValue List<StudentListResponseItem>? = null,

    var studentattendancedata : @RawValue List<StudentAttendanceResponseItem>? =  null,

    var feedbackMaster : @RawValue List<FeedbackMasterDataItem>? = null,

    var studentportfolioresponse : @RawValue List<StudentPortFolioResponseItem>? = null,

    var studentgallerydata :  @RawValue List<StudentGalleryResponseItem>? = null

    ) : Parcelable
