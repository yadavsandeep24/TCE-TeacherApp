package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TopicJsonResponse(

    @Expose
    val asset: List<Asset>,

    @Expose
    val creationDate: Long,

    @Expose
    val gallery: List<String>,

    @Expose
    val practice: String
):Parcelable