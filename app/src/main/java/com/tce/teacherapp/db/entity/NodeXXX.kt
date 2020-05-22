package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "NodeXXX",
    primaryKeys = ["nodexxx_id", "nodexx_id", "nodex_id", "node_id", "book_id"]
)
@Parcelize
data class NodeXXX(

    @ColumnInfo(name = "resource_originator")
    var ResourceOriginator: String,

    @ColumnInfo(name = "tagging_level")
    var TaggingLevel: String,

    @ColumnInfo(name = "content_type")
    var contenttype: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "nodexxx_id")
    var id: String,

    @ColumnInfo(name = "image")
    var image: String?,

    @ColumnInfo(name = "skills")
    var skills: List<String>,

    @ColumnInfo(name = "src")
    var src: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "nodexx_id")
    var nodexxId: String,

    @ColumnInfo(name = "nodex_id")
    var nodexId: String,

    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "book_id")
    var bookId: String
) : Parcelable