package com.tce.teacherapp.db.entity

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DailyPlanner(

    @Expose
    val id :String,

    @Expose
    val date : String,

    @Expose
    val eventList : ArrayList<Event>,

    @Expose
    var birthdayList : ArrayList<StudentBirthDay>,

    @Expose
    val lessonPlan : LessonPlan,

    var eventData : EventData

    ):Parcelable
