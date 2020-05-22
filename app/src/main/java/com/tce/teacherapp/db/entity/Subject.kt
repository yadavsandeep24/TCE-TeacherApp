package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Subject", primaryKeys = ["subject_id", "grade_id"])
@Parcelize
data class Subject(

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "subject_id")
    val id: String,

    @ColumnInfo(name = "subject_index")
    val subjectIndex: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "book_id")
    val bookId: String,

    @ColumnInfo(name = "books_title")
    val booksTitle: String,

    @ColumnInfo(name = "grade_id")
    val gradeId: String

) : Parcelable