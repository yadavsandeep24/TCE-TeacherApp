package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StudentGalleryResponseItem(

    @Expose
    @SerializedName("DataList")
    val studentGalleryDataList: List<StudentGalleryData>,

    @Expose
    val Date: String
)