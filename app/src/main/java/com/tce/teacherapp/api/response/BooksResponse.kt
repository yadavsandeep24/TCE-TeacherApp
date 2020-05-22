package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose

data class BooksResponse(
    @Expose
    val bookId: String,

    @Expose
    val title: String
)