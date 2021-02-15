package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CurriculumVO(

    @Expose
    val curriculumId: String,

    @Expose
    val grades: List<Grade>,

    @Expose
    val levels: List<Level>
):Parcelable