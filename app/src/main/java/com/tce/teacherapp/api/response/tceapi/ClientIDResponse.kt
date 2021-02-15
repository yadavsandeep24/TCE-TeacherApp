package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ClientIDResponse(

    @Expose
    val apiVersion: String,

    @Expose
    val clientTimeout: String,

    @Expose
    val defaultSchool: String,

    @Expose
    val sessionTimeout: String
):Parcelable