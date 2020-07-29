package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(

    @Expose
    val id: Int,

    @Expose
    val eventName: String,

    @Expose
    val imageUrl: String,

    @Expose
    val type: String,
    @Expose
    val eventColor: String,
    @Expose
    val typeColor: String,
    @Expose
    val iconBackColor: String,

    @Expose
    val eventBackColor: String,

    @Expose
    val date: String?,
    @Expose
    val note: String?,
    @Expose
    val eventImageUrl: String?,

    var eventCount: Int



) : Parcelable
