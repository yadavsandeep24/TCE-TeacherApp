package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

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
)