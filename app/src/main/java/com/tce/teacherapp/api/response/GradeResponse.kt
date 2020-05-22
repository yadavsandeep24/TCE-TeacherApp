package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Division
import com.tce.teacherapp.db.entity.Grade
import com.tce.teacherapp.db.entity.Subject

class GradeResponse(

    @Expose
    val divisions: List<Division>,

    @Expose
    val gradeTitle: String,

    @Expose
    val id: String,

    @Expose
    val orderNo: String,

    @Expose
    val schoolGrdId: String,

    @Expose
    val subjects: List<SubjectResponse>
) {
    fun toGrade(): Grade {
        return Grade(
            gradeTitle = gradeTitle,
            id = id,
            orderNo = orderNo,
            schoolGrdId = schoolGrdId,
            subjectList = toSubjectList(id, subjects)
        )
    }

    private fun toSubjectList(
        id: String,
        subjects: List<SubjectResponse>
    ): List<Subject> {
        val tempSubjectList: ArrayList<Subject> = ArrayList()
        for (subjectResponse in subjects) {
            val subject = subjectResponse.toSubject(id)
            tempSubjectList.add(subject)
        }
        return tempSubjectList
    }
}