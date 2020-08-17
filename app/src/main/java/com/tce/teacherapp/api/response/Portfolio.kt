package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Portfolio(

    @Expose
    val Date: String,

    @Expose
    val Feedback: List<Feedback>,

    @Expose
    val Gallery: List<StudentGalleryData>,

    @Expose
    val TeacherNote: String
):Parcelable