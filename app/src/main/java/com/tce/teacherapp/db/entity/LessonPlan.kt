package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LessonPlan (

    @Expose
    val id : String,

    @Expose
    val date : String,

    @Expose
    val isHoliday : Boolean,

    @Expose
    val holidayMessage : String,

    @Expose
    val PeriodList : List<LessonPlanPeriod>

): Parcelable
