package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Student (

    @Expose
    val id: Int,

    @Expose
    val name: String,

    @Expose
    val imageUrl: String,

    var isSelected : Boolean


) : Parcelable
