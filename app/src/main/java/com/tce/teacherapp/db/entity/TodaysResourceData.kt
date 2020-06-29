package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TodaysResourceData(
    val isShowLess  :Boolean,
    val nextEventCount  :Int,
    var todayResource: ArrayList<DashboardResource>
):Parcelable