package com.tce.teacherapp.repository

import android.app.Application
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.ui.dashboard.messages.state.MessageViewState
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.dashboard.students.state.StudentViewState
import com.tce.teacherapp.ui.dashboard.subjects.state.SubjectViewState
import com.tce.teacherapp.util.DataState
import com.tce.teacherapp.util.StateEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    fun getTCEApplication(): Application

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

    fun setFingerPrintMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getTeacherDashboardData(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getParentDashboardData(
        id: Int,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getEventList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getTodayResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getLastViewedResourceList(
        count: Int,
        showOriginal: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun setFaceIdMode(
        checked: Boolean,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun checkLoginMode(
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun updatePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String,
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun updateProfile(
        userInfo: Profile,
        stateMessage: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getUserClassLists(
        stateEvent: StateEvent
    ): Flow<DataState<DashboardViewState>>

    fun getChapterList(
        query: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>


    fun getTopicResourceList(
        query: String,
        topicId: String,
        chapterId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

    fun getPlannerData(
        isShowLess: Boolean,
        stateEvent1: String?,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>>

    fun getMonthlyPlannerData(
        query: String,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>>

    fun setSelectedChildPosition(
        position: Int,
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>>

    fun getSelectedChildPosition(
        stateEvent: StateEvent
    ): Flow<DataState<PlannerViewState>>

    fun getChapterResourceType(
        chapterId: String,
        topicId: String,
        bookId: String,
        stateEvent: StateEvent
    ): Flow<DataState<SubjectViewState>>

    fun getStudentData(stateEvent: StateEvent): Flow<DataState<StudentViewState>>
    fun getStudentAttendanceData(stateEvent: StateEvent): Flow<DataState<StudentViewState>>
    fun getFeedBackMasterData(stateEvent: StateEvent): Flow<DataState<StudentViewState>>
    fun getGalleryData(stateEvent: StateEvent): Flow<DataState<StudentViewState>>
    fun getStudentPortfolio(stateEvent: StateEvent): Flow<DataState<StudentViewState>>


}
