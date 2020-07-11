package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LessonPlanResource (
    @Expose
    val id : String,

    @Expose
    val type : String,

    @Expose
    val contenttype : String,

    @Expose
    val src : String,

    @Expose
    val image : String,

    @Expose
    val title : String,

    @Expose
    val description : String,

    @Expose
    val skills : List<String>,

    @Expose
    val TaggingLevel : String,

    @Expose
    val ResourceOriginator : String,

    @Expose
    val SequenceNo : Int
) : Parcelable