package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class StudentPortFolioResponseItem(

    @Expose
    val DaysAbsent: String,
    @Expose
    val DaysPresent: String,

    @Expose
    val Note: String,

    @Expose
    val Portfolio: List<Portfolio>,

    @Expose
    val id: String
)