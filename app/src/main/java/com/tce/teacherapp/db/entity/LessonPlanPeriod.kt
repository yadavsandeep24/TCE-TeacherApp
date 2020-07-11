package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LessonPlanPeriod (
    @Expose
    val id : String,

    @Expose
    val Topic : String,

    @Expose
    val ShareToAll : Boolean,

    @Expose
    val LessonTypeValue : String,

    @Expose
    val LessonTypeId : String,

    @Expose
    val SequenceNo : Int,

    @Expose
    val Date : String,

    @Expose
    val TeacherFocus : String,

    @Expose
    val Activity : String,

    @Expose
    val Skills : List<String>,

    @Expose
    val ResourceList : List<LessonPlanResource>

):Parcelable
