package com.tce.teacherapp.repository

import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getGrades(
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

    fun getSubjects(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

    fun setSelectedGrade(
        grade: Grade,
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

    fun getTopicList(
        query: String,
        bookID: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

}
