package com.tce.teacherapp.ui.dashboard.subjects.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.tceapi.CurriculumVO
import com.tce.teacherapp.api.response.tceapi.TCEBookResponse
import com.tce.teacherapp.api.response.tceapi.TCETopicResponseItem
import com.tce.teacherapp.db.entity.*
import kotlinx.android.parcel.Parcelize

const val SUBJECT_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.SubjectViewState"

@Parcelize
data class SubjectViewState(
    var gradeList: List<Grade>? = null,
    var selectedGradePosition: Int? = 0,
    var subjectList: List<Subject>? = null,
    var topicList: List<Topic>? = null,
    var chapterLearnData: ChapterLearnData? = null,
    var chapterResourceTyeList: List<ChapterResourceType>? = null,
    var optionList:List<OptionListData>? = null,
    var allTopicResourceList:List<Resource>? = null,
    var curriculumVo: CurriculumVO? = null,
    var tceBookResponse: TCEBookResponse? = null,
    var tceTopicResponse: TCETopicResponseItem? = null,
    var tceTopicPracticeResponse: TCETopicResponseItem? = null
) : Parcelable



