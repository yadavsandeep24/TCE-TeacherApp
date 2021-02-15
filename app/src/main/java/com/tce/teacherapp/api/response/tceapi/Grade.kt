package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Grade(

    @Expose
    val divisions: List<String>,

    @Expose
    val gradeId: String,

    @Expose
    val gradeTitle: String,

    @Expose
    val orderNo: String,

    @Expose
    val schoolGrdId: String,

    @Expose
    val subjects: List<Subject>
):Parcelable