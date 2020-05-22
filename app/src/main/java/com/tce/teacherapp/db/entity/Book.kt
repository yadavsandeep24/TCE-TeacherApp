package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "Book")
@Parcelize
data class Book(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "book_id")
    var id: String,

    @ColumnInfo(name = "label")
    var label: String,

    @Ignore
    var node: List<Node>,

    @ColumnInfo(name = "type")
    var type: String
) : Parcelable {
    constructor() : this("", "", emptyList(), "")
}