package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.Grade

class GradeListResponse(

    @SerializedName("")
    @Expose
    var grades: List<GradeResponse>
) {
    fun toGradeList(grades: List<GradeResponse>): List<Grade> {
        val gradeList: ArrayList<Grade> = ArrayList()
        for (gradeResponse in grades) {
            val grade = gradeResponse.toGrade()
            gradeList.add(
                gradeResponse.toGrade()
            )
        }
        return gradeList
    }
}