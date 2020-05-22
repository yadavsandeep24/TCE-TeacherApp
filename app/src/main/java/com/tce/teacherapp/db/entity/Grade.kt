package com.tce.teacherapp.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Grade")
@Parcelize
data class Grade(

    @ColumnInfo(name = "grade_title")
    var gradeTitle: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "grade_id")
    var id: String,

    @ColumnInfo(name = "order_no")
    var orderNo: String,

    @ColumnInfo(name = "school_Grade_id")
    var schoolGrdId: String,

    @Ignore
    var subjectList: List<Subject>

) : Parcelable {
    constructor() : this("", "", "", "", emptyList())
}