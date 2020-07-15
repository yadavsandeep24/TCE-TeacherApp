package com.tce.teacherapp.ui.dashboard.planner.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.*
import kotlinx.android.parcel.Parcelize

const val PLANNER_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class PlannerViewState(
    var eventData : EventData? = null,
    var eventList: List<Event>? = null,
    var birthdayList: List<StudentBirthDay>? = null,
    var lessonPlanList: List<LessonPlan>? = null,
    var dailyPlannerList : List<DailyPlanner>? = null,
    var monthlyPlannerList : List<MonthlyPlanner>? = null

) : Parcelable
