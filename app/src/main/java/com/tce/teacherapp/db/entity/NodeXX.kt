package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "NodeXX", primaryKeys = ["nodexx_id", "nodex_id", "node_id", "book_id"])
@Parcelize
data class NodeXX(

    @ColumnInfo(name = "icon")
    var icon: String?,

    @ColumnInfo(name = "nodexx_id")
    var id: String,

    @ColumnInfo(name = "label")
    var label: String,

    @Ignore
    var node: List<NodeXXX>,

    @ColumnInfo(name = "treatment")
    var treatment: String?,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "nodex_id")
    var nodexId: String,

    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "book_id")
    var bookId: String
) : Parcelable {
    constructor() : this("", "", "", emptyList(), "", "", "", "", "")
}