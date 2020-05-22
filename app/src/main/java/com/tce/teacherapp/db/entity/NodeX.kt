package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "NodeX", primaryKeys = ["nodex_id", "node_id", "book_id"])
@Parcelize
data class NodeX(
    @ColumnInfo(name = "icon")
    var icon: String?,

    @ColumnInfo(name = "nodex_id")
    var id: String,

    @ColumnInfo(name = "image")
    var image: String?,

    @ColumnInfo(name = "label")
    var label: String,

    @Ignore
    var node: List<NodeXX>,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "book_id")
    var bookId: String
) : Parcelable {
    constructor() : this("", "", "", "", emptyList(), "", "", "")
}