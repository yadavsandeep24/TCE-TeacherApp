package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LatestUpdateSubList(

    @Expose
    val attendance: String,

    @Expose
    val resourceList: ArrayList<DashboardResourceType>,

    @Expose
    val eventList: ArrayList<Event>

): Parcelable