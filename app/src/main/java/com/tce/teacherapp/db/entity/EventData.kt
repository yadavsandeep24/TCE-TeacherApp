package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventData(
    val isShowLess  :Boolean,
    val nextEventCount  :Int,
    var eventList: ArrayList<Event>
):Parcelable