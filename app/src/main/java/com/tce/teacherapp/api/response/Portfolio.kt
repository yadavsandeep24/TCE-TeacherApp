package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Portfolio(

    @Expose
    val Date: String,

    @Expose
    var Feedback: List<Feedback>,

    @Expose
    var Gallery: List<StudentGalleryData>,

    @Expose
    var TeacherNote: String,

    var isGalleryCheckBoxSelected: Boolean
):Parcelable