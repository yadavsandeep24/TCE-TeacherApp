package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class BookJsonResponse(

    @Expose
    val bookId: String,

    @Expose
    val booktree: Booktree,

    @Expose
    val grade: String,

    @Expose
    val subject: String
):Parcelable