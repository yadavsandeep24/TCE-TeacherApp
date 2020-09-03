package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AbsentData(
    @Expose
    val Date: String,

    @Expose
    val IsAbsent: Boolean,

    @Expose
    val IsHoliday: Boolean,

    @Expose
    val Msg: String,

    @Expose
    val id: String
):Parcelable