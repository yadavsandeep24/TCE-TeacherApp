package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StudentAttendanceResponseItem(

    @Expose
    @SerializedName("DataList")
    val studentList: List<StudentAttendanceData>,

    @Expose
    val Date: String,

    @Expose
    val id: String
)