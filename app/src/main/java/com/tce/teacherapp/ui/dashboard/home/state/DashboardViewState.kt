package com.tce.teacherapp.ui.dashboard.home.state

import android.os.Parcelable
import com.tce.teacherapp.db.entity.Profile
import kotlinx.android.parcel.Parcelize

const val DASHBOARD_VIEW_STATE_BUNDLE_KEY = "com.tce.teacherapp.ui.auth.state.AuthViewState"

@Parcelize
data class DashboardViewState (
    var profile: Profile?= null
) : Parcelable
