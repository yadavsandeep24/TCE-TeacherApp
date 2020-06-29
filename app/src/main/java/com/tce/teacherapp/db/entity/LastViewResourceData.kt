package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LastViewResourceData(
    val isShowLess  :Boolean,
    val nextEventCount  :Int,
    var lastViewResourceList: ArrayList<DashboardResourceType>
):Parcelable