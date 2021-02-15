package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class ContentType(

    @Expose
    val key: String,

    @Expose
    val value: String
)