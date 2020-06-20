package com.tce.teacherapp.ui.dashboard.home.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.DashboardResource
import com.tce.teacherapp.db.entity.DashboardResourceType
import com.tce.teacherapp.db.entity.Event
import com.tce.teacherapp.db.entity.Profile
import kotlinx.android.parcel.Parcelize

const val DASHBOARD_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class DashboardViewState (
    var profile: Profile?= null,
    var isFingerPrintLoginEnabled: Boolean? = null,
    var eventList : ArrayList<Event>? = null,
    var todayResourceList : ArrayList<DashboardResource>? = null,
    var lastViewedResourceList : ArrayList<DashboardResourceType>? = null
) : Parcelable
