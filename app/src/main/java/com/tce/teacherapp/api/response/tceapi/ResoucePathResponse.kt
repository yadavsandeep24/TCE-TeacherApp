package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.Expose

data class ResoucePathResponse(

    @Expose
    val error: String,

    @Expose
    val message: String,

    @Expose
    val path: String,

    @Expose
    val status: Int,

    @Expose
    val timestamp: String
)