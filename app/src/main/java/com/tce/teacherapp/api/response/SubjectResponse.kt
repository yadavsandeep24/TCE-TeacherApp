package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.tce.teacherapp.db.entity.Subject


class SubjectResponse(

    @Expose
    val books: BooksResponse,

    @Expose
    var icon: String,

    @Expose
    var id: String,

    @Expose
    var index: Int,

    @Expose
    var title: String
) {

    fun toSubject(gradeId: String): Subject {
        return Subject(
            id = id,
            icon = icon,
            title = title,
            subjectIndex = index,
            gradeId = gradeId,
            bookId = books.bookId,
            booksTitle = books.title
        )
    }
}