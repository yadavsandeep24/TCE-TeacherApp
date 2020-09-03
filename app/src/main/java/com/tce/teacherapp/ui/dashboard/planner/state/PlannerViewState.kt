package com.tce.teacherapp.ui.dashboard.planner.state

import android.os.Parcelable
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.db.entity.DailyPlanner
import com.tce.teacherapp.db.entity.MonthlyPlanner
import kotlinx.android.parcel.Parcelize

const val PLANNER_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class PlannerViewState(
    var dailyPlannerList : List<DailyPlanner>? = null,
    var monthlyPlannerList : List<MonthlyPlanner>? = null,
    var childList : ArrayList<StudentListResponseItem>? = null

) : Parcelable
