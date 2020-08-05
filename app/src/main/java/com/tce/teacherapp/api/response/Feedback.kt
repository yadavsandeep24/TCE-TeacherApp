package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class Feedback(

    @Expose
    val Id: String,

    @Expose
    val Name: String
)