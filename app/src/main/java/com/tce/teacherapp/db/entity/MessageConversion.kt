package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageConversion(

    @Expose
    val id: Int,

    @Expose
    val message: String,

    @Expose
    val videoUrl: String,

    @Expose
    val videoTitle: String,

    @Expose
    val videoSubTitle: String,

    @Expose
    val imageUrl: String,

    @Expose
    val time: String,

    @Expose
    val messageType: String,

    @Expose
    val messageFrom: String

) : Parcelable