package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SharedListItem(

    @Expose
    val Name: String,

    @Expose
    val id: String
):Parcelable