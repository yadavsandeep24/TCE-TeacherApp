package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class StudentGalleryResponseItem(

    @Expose
    val studentGalleryDataList: List<StudentGalleryData>,

    @Expose
    val Date: String
)