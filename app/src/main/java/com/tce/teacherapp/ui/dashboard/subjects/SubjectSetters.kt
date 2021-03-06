package com.tce.teacherapp.ui.dashboard.subjects

import com.tce.teacherapp.api.response.tceapi.CurriculumVO
import com.tce.teacherapp.api.response.tceapi.TCEBookResponse
import com.tce.teacherapp.api.response.tceapi.TCETopicResponseItem
import com.tce.teacherapp.db.entity.*
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
fun SubjectsViewModel.setChapterLearnData(it: ChapterLearnData) {
    val update = getCurrentViewStateOrNew()
    update.chapterLearnData = it
    setViewState(update)
}


@FlowPreview
@UseExperimental(ExperimentalCoroutinesApi::class)
fun SubjectsViewModel.setChapterResourceTypeData(it: List<ChapterResourceType>) {
    val update = getCurrentViewStateOrNew()
    update.chapterResourceTyeList = it
    setViewState(update)
}

 @ExperimentalCoroutinesApi
 fun SubjectsViewModel.setAllTopicResourceList(it: List<Resource>){
    val update = getCurrentViewStateOrNew()
    update.allTopicResourceList = it
    setViewState(update)
}
@ExperimentalCoroutinesApi
fun SubjectsViewModel.setCurriculumData(it: CurriculumVO){
    val update = getCurrentViewStateOrNew()
    update.curriculumVo = it
    setViewState(update)
}

@ExperimentalCoroutinesApi
fun SubjectsViewModel.setTCEBookResponse(it: TCEBookResponse) {
    val update = getCurrentViewStateOrNew()
    update.tceBookResponse = it
    setViewState(update)
}

fun SubjectsViewModel.setTceTopicResponse(it: TCETopicResponseItem){
    val update = getCurrentViewStateOrNew()
    update.tceTopicResponse = it
    setViewState(update)

}fun SubjectsViewModel.setTceTopicPracticeResponse(it: TCETopicResponseItem){
    val update = getCurrentViewStateOrNew()
    update.tceTopicPracticeResponse = it
    setViewState(update)
}