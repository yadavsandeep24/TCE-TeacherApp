package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DashboardLatestUpdate(

    @Expose
    val id: String,

    @Expose
    val title: String,

    @Expose
    val subtitle: String,

    @Expose
    val icon: String,

    @Expose
    val subList : LatestUpdateSubList

): Parcelable