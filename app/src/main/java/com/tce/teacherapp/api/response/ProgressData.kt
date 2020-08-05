package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class ProgressData(

    @Expose
    val Id: String,

    @Expose
    val Name: String,

    @Expose
    val Objectives: List<Objective>,

    @Expose
    val activepage: Int,

    @Expose
    val isCompleted: Boolean,

    @Expose
    val sequenceNo: Int,

    @Expose
    val totalpages: Int
)