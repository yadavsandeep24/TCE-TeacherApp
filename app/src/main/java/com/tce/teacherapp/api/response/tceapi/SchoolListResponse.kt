package com.tce.teacherapp.api.response.tceapi

import com.google.gson.annotations.Expose

data class SchoolListResponse(

    @Expose
    val query: String,

    @Expose
    val suggestions: List<String>
)