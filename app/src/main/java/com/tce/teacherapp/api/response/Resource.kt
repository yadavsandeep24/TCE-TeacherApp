package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Resource(

    @Expose
    val FileName: String?,

    @Expose
    val contenttype: String?,

    @Expose
    val id: String?,

    @Expose
    val src: String?,

    @Expose
    val title: String?,

    @Expose
    val type: String?,

    @Expose
    val image: String?
) : Parcelable