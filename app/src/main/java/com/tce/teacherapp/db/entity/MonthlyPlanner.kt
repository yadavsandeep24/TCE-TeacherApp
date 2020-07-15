package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MonthlyPlanner(

    @Expose
    val id :Int,

    @Expose
    val date : String,

    @Expose
    val eventList : ArrayList<Event>

    ):Parcelable
