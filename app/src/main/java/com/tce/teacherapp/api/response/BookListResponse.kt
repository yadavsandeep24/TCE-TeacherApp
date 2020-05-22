package com.tce.teacherapp.api.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tce.teacherapp.db.entity.Book

class BookListResponse(
    @SerializedName("")
    @Expose
    var books: List<BookResponse>
) {
    fun toBookList(book: List<BookResponse>): List<Book> {
        val bookList: ArrayList<Book> = ArrayList()
        for (bookResponse in books) {
            bookList.add(bookResponse.toBook())
        }
        return bookList
    }
}