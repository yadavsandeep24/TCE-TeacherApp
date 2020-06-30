package com.tce.teacherapp.ui.dashboard.home.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.*
import kotlinx.android.parcel.Parcelize

const val DASHBOARD_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class DashboardViewState (
    var profile: Profile?= null,
    var isFingerPrintLoginEnabled: Boolean? = null,
    var isFaceLoginEnabled: Boolean? = null,
    var eventData : EventData? = null,
    var todayResourceData: TodaysResourceData? = null,
    var lastViewedResourceData : LastViewResourceData? = null,
    var isPasswordUpdated: Boolean? = null,
    var classList: List<ClassListsItem>? = null,
    var latestUpdateList : ArrayList<DashboardLatestUpdate>? =null,
    var childList : ArrayList<Student>? =null
) : Parcelable
