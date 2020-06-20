package com.tce.teacherapp.repository

import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
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

    fun getMessage(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>>

    fun getStudentList(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>>

    fun getResourceList(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>>

    fun getSelectedResourceList(
        query: String,
        typeId: Int,
        stateEvent: StateEvent
    ): Flow<DataState<MessageViewState>>

    fun getProfile(
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun updateProfilePic(
        resultUri: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun setFingerPrintMode(checked: Boolean)
    fun checkFingerPrintEnableMode(
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getDashboardData(
        count : Int,
        type: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>


    fun getEventList(
        count : Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getTodayResourceList(
        count : Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getLastViewedResourceList(
        count : Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

}
