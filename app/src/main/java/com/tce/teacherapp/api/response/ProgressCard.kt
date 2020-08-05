package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class ProgressCard(

    @Expose
    val Grade: String,

    @Expose
    val ProgressData: List<ProgressData>,

    @Expose
    val Term: String,

    @Expose
    val id: Int
)