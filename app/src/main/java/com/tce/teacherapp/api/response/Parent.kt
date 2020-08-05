package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class Parent(

    @Expose
    val ContactNo: String,

    @Expose
    val EmailId: String,

    @Expose
    val FirstName: String,

    @Expose
    val LastName: String,

    @Expose
    val Relation: String
)