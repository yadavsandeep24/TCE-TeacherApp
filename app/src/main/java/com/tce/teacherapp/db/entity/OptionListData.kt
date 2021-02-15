package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OptionListData(
    val name  :String,
    val isSelected  :Boolean
):Parcelable