package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Level(

    @Expose
    val levelId: String,

    @Expose
    val levelTitle: String,

    @Expose
    val subjects: List<Subject>
):Parcelable