package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Feedback(

    @Expose
    val Id: String,

    @Expose
    val Name: String
):Parcelable