package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class NodeXX(

    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeXXX>,

    @Expose
    val type: String
):Parcelable