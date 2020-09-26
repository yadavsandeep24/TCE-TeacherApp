package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageResource (
    val id : Int,
    val videoUrl : String,
    val videoThumb : String,
    val title : String,
    val typeId : Int,
    val Type : String,
    val isAdded : Boolean,
    val isSelected : Boolean
): Parcelable
