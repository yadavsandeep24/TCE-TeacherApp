package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "ChapterResourceType", primaryKeys = ["chapterResourceType_id", "chapter_id", "topic_id", "book_id"])
@Parcelize
data class ChapterResourceType(

    @ColumnInfo(name = "icon")
    var icon: String?,

    @ColumnInfo(name = "chapterResourceType_id")
    var id: String,

    @ColumnInfo(name = "label")
    var label: String,

    @Ignore
    var resourceList: List<Resource>,

    @ColumnInfo(name = "treatment")
    var treatment: String?,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "chapter_id")
    var chapterId: String,

    @ColumnInfo(name = "topic_id")
    var topicId: String,

    @ColumnInfo(name = "book_id")
    var bookId: String
) : Parcelable {
    constructor() : this("", "", "", emptyList(), "", "", "", "", "")
}