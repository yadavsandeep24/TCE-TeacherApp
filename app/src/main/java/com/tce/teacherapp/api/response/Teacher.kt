package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Teacher(

    @Expose
    val ContactNo: String,

    @Expose
    val EmailId: String,

    @Expose
    val FirstName: String,

    @Expose
    val LastName: String,

    @Expose
    val ProfilePic: String
):Parcelable