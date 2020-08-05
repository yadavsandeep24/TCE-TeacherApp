package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class StudentAttendanceData(

    @Expose
    val Status: Boolean,

    @Expose
    val StudentId: String
)