package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DashboardResource (
    val resId : Int,
    val title : String,
    val subTitle : String,
    val typeList : List<DashboardResourceType>

): Parcelable
