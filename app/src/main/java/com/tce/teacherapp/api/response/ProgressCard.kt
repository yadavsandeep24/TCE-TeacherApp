package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProgressCard(

    @Expose
    val Grade: String,

    @Expose
    val ProgressData: List<ProgressData>,

    @Expose
    val Term: String,

    @Expose
    val id: Int
):Parcelable