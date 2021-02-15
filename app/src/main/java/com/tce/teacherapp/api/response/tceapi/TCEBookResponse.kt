package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TCEBookResponse(

    @Expose
    val bookId: String,

    @Expose
    val iconsPath: String,

    @Expose
    val json: String,

    @Expose
    val title: String,

    @Ignore
    var booJsonResponse : BookJsonResponse,

    @Ignore
    var accessToken :String


):Parcelable