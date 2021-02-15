package com.tce.teacherapp.api.response.tceapi

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ActivityVo(

    @Expose
    val access_token: String,

    @Expose
    val expires_in: String,

    @Expose
    val isDigitalAsset: Boolean,

    @Expose
    val list: List<Asset>,

    @Expose
    val nativeType: String,

    @Expose
    val refresh_token: String
):Parcelable