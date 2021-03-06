package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Topic", primaryKeys = ["topic_id", "book_id"])
@Parcelize
data class Topic(

    @ColumnInfo(name = "icon")
    var icon: String?,

    @ColumnInfo(name = "topic_id")
    var id: String,

    @ColumnInfo(name = "index")
    var index: Int,

    @ColumnInfo(name = "label")
    var label: String,

    @ColumnInfo(name = "menu_type")
    var menutype: String,

    @Ignore
    var chapterList: List<Chapter>,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "section")
    var section: String?,

    @ColumnInfo(name = "book_id")
    var bookId: String

) : Parcelable {
    constructor() : this("", "", 0, "", "", emptyList(), "", "","")
}