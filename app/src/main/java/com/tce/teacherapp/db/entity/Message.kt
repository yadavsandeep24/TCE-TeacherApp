package com.tce.teacherapp.db.entity

import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(

    @Expose
    val id: Int,

    @Expose
    val title: String,

    @Expose
    val detail: String,

    @Expose
    val icon: String,

    @Expose
    val time: String,

    @Expose
    val count: String
) : Parcelable