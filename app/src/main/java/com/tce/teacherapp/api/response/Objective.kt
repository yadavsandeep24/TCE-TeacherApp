package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class Objective(

    @Expose
    val Id: String,

    @Expose
    val Name: String,

    @Expose
    val page: Int,

    @Expose
    val value: String
)