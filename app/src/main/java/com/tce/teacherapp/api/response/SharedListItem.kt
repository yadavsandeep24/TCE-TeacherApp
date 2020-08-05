package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class SharedListItem(

    @Expose
    val Name: String,

    @Expose
    val id: String
)