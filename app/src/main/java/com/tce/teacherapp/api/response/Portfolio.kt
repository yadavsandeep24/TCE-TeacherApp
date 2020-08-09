package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class Portfolio(

    @Expose
    val Date: String,

    @Expose
    val Feedback: List<Feedback>,

    @Expose
    val Gallery: List<StudentGalleryData>,

    @Expose
    val TeacherNote: String
)