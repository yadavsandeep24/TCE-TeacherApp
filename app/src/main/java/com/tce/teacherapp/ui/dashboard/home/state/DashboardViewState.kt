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
    var eventList : ArrayList<Event>? = null,
    var todayResourceList : ArrayList<DashboardResource>? = null,
    var lastViewedResourceList : ArrayList<DashboardResourceType>? = null,
    var isPasswordUpdated: Boolean? = null,
    var classList: List<ClassListsItem>? = null
) : Parcelable
