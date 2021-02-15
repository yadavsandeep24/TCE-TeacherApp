package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Subject(

    @Expose
    val books: Books,

    @Expose
    val subjectId: String,

    @Expose
    val title: String
):Parcelable