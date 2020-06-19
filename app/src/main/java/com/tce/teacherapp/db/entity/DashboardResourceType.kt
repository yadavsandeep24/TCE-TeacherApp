package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DashboardResourceType (
    val id : Int,
    val videoUrl : String,
    val videoThumb : String,
    val imageUrl : String,
    val icon : String,
    val title : String,
    val typeId : Int,
    val Type : String,
    val isAdded : Boolean

): Parcelable
