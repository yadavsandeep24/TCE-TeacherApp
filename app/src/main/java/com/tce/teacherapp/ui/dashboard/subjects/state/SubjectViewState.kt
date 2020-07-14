package com.tce.teacherapp.ui.dashboard.subjects.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.ChapterLearnData
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Subject
import com.tce.teacherapp.db.entity.Topic
import kotlinx.android.parcel.Parcelize

const val SUBJECT_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.SubjectViewState"

@Parcelize
data class SubjectViewState(
    var gradeList: List<Grade>? = null,
    var selectedGradePosition: Int? = 0,
    var subjectList: List<Subject>? = null,
    var topicList: List<Topic>? = null,
    var chapterLearnData: ChapterLearnData? = null
) : Parcelable



