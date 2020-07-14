package com.tce.teacherapp.db.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChapterLearnData (
    val isLearn: Boolean,
    var chapterList : List<Chapter>?,
    var resourceList :List<Resource>?
):Parcelable