package com.tce.teacherapp.api.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentGalleryData(

    @Expose
    val sharedItemList: List<SharedListItem>,

    @Expose
    val contenttype: String,

    @Expose
    val description: String,

    @Expose
    val id: String,

    @Expose
    val image: String,

    @Expose
    val src: String,

    @Expose
    val title: String,

    @Expose
    val type: String
):Parcelable