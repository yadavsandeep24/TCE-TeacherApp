package com.tce.teacherapp.db.entity

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
    val count: String,

    @Expose
    val NoOfMember: Int,

    @Expose
    val date: String,

    @Expose
    val conversionList : List<MessageConversion>

) : Parcelable