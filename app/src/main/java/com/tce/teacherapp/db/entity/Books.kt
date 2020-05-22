package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Books(

    @Expose
    val bookId: String,

    @Expose
    val title: String
) : Parcelable