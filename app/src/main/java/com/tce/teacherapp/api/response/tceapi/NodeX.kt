package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class NodeX(

    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeXX>,

    @Expose
    val type: String
):Parcelable