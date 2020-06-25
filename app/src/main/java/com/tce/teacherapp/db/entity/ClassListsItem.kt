package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClassListsItem(

    @Expose
    val id: String,

    @Expose
    val imageUrl: String,

    @Expose
    val name: String,

    @Expose
    val shortName: String
): Parcelable