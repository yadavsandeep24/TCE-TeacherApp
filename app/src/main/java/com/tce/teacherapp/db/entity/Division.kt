package com.tce.teacherapp.db.entity

import com.google.gson.annotations.Expose

data class Division(

    @Expose
    val divisionTitle: String,

    @Expose
    val gradeTitle: String
)