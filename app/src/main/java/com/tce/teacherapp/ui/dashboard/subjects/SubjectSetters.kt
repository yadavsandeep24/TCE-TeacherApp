package com.tce.teacherapp.ui.dashboard.subjects

import com.tce.teacherapp.db.entity.Chapter
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Subject
import com.tce.teacherapp.db.entity.Topic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setGradeListData(gradeList: List<Grade>) {
    val update = getCurrentViewStateOrNew()
    update.gradeList = gradeList
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setSubjectListData(it: List<Subject>) {
    val update = getCurrentViewStateOrNew()
    update.subjectList = it
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setSelectedGradePosition(it: Int) {
    val update = getCurrentViewStateOrNew()
    update.selectedGradePosition = it
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setTopicListData(it: List<Topic>) {
    val update = getCurrentViewStateOrNew()
    update.topicList = it
    setViewState(update)
}

@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setChapterListData(it: List<Chapter>) {
    val update = getCurrentViewStateOrNew()
    update.chapterList = it
    setViewState(update)
}


