package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Node(

    @Expose
    val id: String,

    @Expose
    val label: String,

    @Expose
    val node: List<NodeX>,

    @Expose
    val type: String
):Parcelable