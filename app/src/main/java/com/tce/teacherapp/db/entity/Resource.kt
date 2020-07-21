package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "Resource",
    primaryKeys = ["resource_id", "chapterResourceType_id","chapter_id"]
)
@Parcelize
data class Resource(

    @ColumnInfo(name = "resource_originator")
    var ResourceOriginator: String,

    @ColumnInfo(name = "tagging_level")
    var TaggingLevel: String,

    @ColumnInfo(name = "content_type")
    var contenttype: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "resource_id")
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

    @ColumnInfo(name = "chapterResourceType_id")
    var chapterResourceTypeId: String,

    @ColumnInfo(name = "chapter_id")
    var chapterId: String

) : Parcelable